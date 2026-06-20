#!/usr/bin/env python3
"""Compile Java files with lombok annotation processor"""
import subprocess, os, sys, shutil, time

def run(cmd, timeout=600):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    return (r.stdout.decode('utf-8', errors='ignore').strip() + 
            (r.stderr.decode('utf-8', errors='ignore').strip() if r.stderr else '')), r.returncode

def main():
    base = "/root/bigdata-portal-platform"
    backend_dir = os.path.join(base, "backend")
    src_dir = os.path.join(backend_dir, "src/main/java")
    work_dir = "/tmp/java_build3"

    # Clean and create workspace
    print("=== Step 1: Prepare workspace")
    if os.path.exists(work_dir):
        shutil.rmtree(work_dir)
    os.makedirs(work_dir)
    os.makedirs(os.path.join(work_dir, "classes"))

    # Download lombok if needed
    lombok_path = "/tmp/lombok.jar"
    if not os.path.exists(lombok_path) or os.path.getsize(lombok_path) < 100000:
        print("Downloading lombok...")
        out, rc = run("curl -sL -o /tmp/lombok.jar 'https://repo1.maven.org/maven2/org/projectlombok/lombok/1.18.28/lombok-1.18.28.jar'")
        print(f"Download rc={rc}, size={os.path.getsize(lombok_path) if os.path.exists(lombok_path) else 0}")

    # Extract dependencies from existing JAR
    print("\n=== Step 2: Extract dependencies")
    jar_path = os.path.join(backend_dir, "portal-2.0.0.jar")
    if not os.path.exists(jar_path):
        jar_path = os.path.join(backend_dir, "target", "portal-2.0.0.jar")

    lib_dir = os.path.join(work_dir, "libs")
    class_dir = os.path.join(work_dir, "orig_classes")
    os.makedirs(lib_dir, exist_ok=True)
    os.makedirs(class_dir, exist_ok=True)

    run(f"cd {lib_dir} && unzip -q -o {jar_path} 'BOOT-INF/lib/*' 2>/dev/null")
    run(f"cd {class_dir} && unzip -q -o {jar_path} 'BOOT-INF/classes/*' 2>/dev/null")

    cp_libs = os.path.join(lib_dir, "BOOT-INF", "lib")
    cp_classes = os.path.join(class_dir, "BOOT-INF", "classes")

    # Get all .java files
    print("\n=== Step 3: Find Java files")
    java_files = []
    for root, dirs, files in os.walk(src_dir):
        for f in files:
            if f.endswith(".java"):
                java_files.append(os.path.join(root, f))
    print(f"Found {len(java_files)} Java source files")

    # Write file list
    filelist = os.path.join(work_dir, "sources.txt")
    with open(filelist, "w") as f:
        for jf in java_files:
            f.write(jf + "\n")

    # Compile with lombok annotation processor
    print("\n=== Step 4: Compile with lombok")
    cmd = (f"javac -encoding UTF-8 -source 1.8 -target 1.8 "
           f"-cp '{cp_libs}/*:{cp_classes}' "
           f"-processorpath /tmp/lombok.jar "
           f"-d {work_dir}/classes "
           f"@{filelist} 2>&1 | head -80")
    out, rc = run(cmd)
    print(out)
    print(f"Compile exit code: {rc}")

    # Check generated classes
    class_count = 0
    for r, d, fs in os.walk(os.path.join(work_dir, "classes")):
        for f2 in fs:
            if f2.endswith(".class"):
                class_count += 1
    print(f"Generated {class_count} .class files")

    if class_count == 0:
        print("ERROR: No class files generated!")
        sys.exit(1)

    # Rebuild JAR
    print("\n=== Step 5: Rebuild JAR")
    jar_root = os.path.join(work_dir, "jar_root")
    if os.path.exists(jar_root):
        shutil.rmtree(jar_root)
    os.makedirs(jar_root)

    # Re-extract everything from original JAR
    run(f"cd {jar_root} && unzip -q -o {jar_path} 2>&1")

    # Replace BOOT-INF/classes with our compiled version
    target_boot_classes = os.path.join(jar_root, "BOOT-INF", "classes")
    if os.path.exists(target_boot_classes):
        shutil.rmtree(target_boot_classes)
    shutil.copytree(os.path.join(work_dir, "classes"), target_boot_classes)
    print("Replaced BOOT-INF/classes with newly compiled classes")

    # Create new JAR using jar command
    new_jar = "/tmp/portal-2.0.0.jar"
    if os.path.exists(new_jar):
        os.remove(new_jar)

    os.chdir(jar_root)
    out, rc = run("jar cf /tmp/portal-2.0.0.jar . 2>&1")
    print(out)

    if os.path.exists(new_jar):
        size = os.path.getsize(new_jar)
        print(f"New JAR size: {size} bytes")

        # Copy to backend location
        shutil.copy2(new_jar, os.path.join(backend_dir, "portal-2.0.0.jar"))
        os.makedirs(os.path.join(backend_dir, "target"), exist_ok=True)
        shutil.copy2(new_jar, os.path.join(backend_dir, "target", "portal-2.0.0.jar"))
        print("JAR copied to backend/")

        # Rebuild Docker image
        print("\n=== Step 6: Rebuild backend Docker image")
        os.chdir(base)
        out, rc = run("docker compose build backend 2>&1 | tail -20")
        print(out)

        # Restart backend
        print("\n=== Step 7: Restart backend")
        out, rc = run("docker compose up -d backend 2>&1 | tail -10")
        print(out)

        # Wait for health
        print("\n=== Step 8: Wait for health check")
        for i in range(40):
            out, rc = run("docker inspect -f '{{.State.Health.Status}}' bigdata-backend 2>&1")
            if "healthy" in out:
                print(f"Backend healthy after {i*5}s")
                break
            time.sleep(5)
        else:
            print(f"Status: {out}")

        print("\n=== DEPLOYMENT COMPLETE!")
    else:
        print("ERROR: Failed to create JAR")
        sys.exit(1)

if __name__ == "__main__":
    main()
