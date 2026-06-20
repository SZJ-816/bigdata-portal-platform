#!/usr/bin/env python3
"""Compile Java files on the CentOS host and deploy"""
import subprocess, os, sys, shutil, time

def run(cmd, timeout=600):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    return (r.stdout.decode('utf-8', errors='ignore').strip() + 
            (r.stderr.decode('utf-8', errors='ignore').strip() if r.stderr else '')), r.returncode

def main():
    base = "/root/bigdata-portal-platform"
    backend_dir = os.path.join(base, "backend")
    src_dir = os.path.join(backend_dir, "src/main/java")
    work_dir = "/tmp/java_build2"

    # Clean and create workspace
    print("=== Step 1: Prepare workspace")
    if os.path.exists(work_dir):
        shutil.rmtree(work_dir)
    os.makedirs(work_dir)
    os.makedirs(os.path.join(work_dir, "classes"))
    print(f"Workspace: {work_dir}")

    # Extract dependencies from existing JAR
    print("\n=== Step 2: Extract dependencies")
    jar_path = os.path.join(backend_dir, "portal-2.0.0.jar")
    if not os.path.exists(jar_path):
        jar_path = os.path.join(backend_dir, "target", "portal-2.0.0.jar")

    lib_dir = os.path.join(work_dir, "libs")
    class_dir = os.path.join(work_dir, "orig_classes")
    os.makedirs(lib_dir, exist_ok=True)
    os.makedirs(class_dir, exist_ok=True)

    out, rc = run(f"cd {lib_dir} && unzip -q -o {jar_path} 'BOOT-INF/lib/*' 2>/dev/null")
    print(f"Extract libs: rc={rc}")
    out, rc = run(f"cd {class_dir} && unzip -q -o {jar_path} 'BOOT-INF/classes/*' 2>/dev/null")
    print(f"Extract classes: rc={rc}")

    # Count libs
    lib_count = 0
    for r, d, f in os.walk(lib_dir):
        for f2 in f:
            if f2.endswith(".jar"):
                lib_count += 1
    print(f"Extracted {lib_count} library JARs")

    # Build classpath
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

    # Write file list for javac
    filelist = os.path.join(work_dir, "sources.txt")
    with open(filelist, "w") as f:
        for jf in java_files:
            f.write(jf + "\n")

    # Compile!
    print("\n=== Step 4: Compile with javac 1.8")
    cmd = (f"javac -encoding UTF-8 -source 1.8 -target 1.8 "
           f"-cp '{cp_libs}/*:{cp_classes}' "
           f"-d {work_dir}/classes "
           f"@{filelist} 2>&1 | tail -40")
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
        print("ERROR: No class files!")
        sys.exit(1)

    # Now rebuild the JAR
    print("\n=== Step 5: Rebuild JAR")
    # Copy new classes over old ones
    for root, dirs, files in os.walk(os.path.join(work_dir, "classes")):
        for f in files:
            src = os.path.join(root, f)
            rel = os.path.relpath(src, os.path.join(work_dir, "classes"))
            dst = os.path.join(cp_classes, rel)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copy2(src, dst)
    print("Classes merged into BOOT-INF/classes")

    # Rebuild the JAR preserving the original structure
    jar_root = os.path.join(work_dir, "jar_root")
    if os.path.exists(jar_root):
        shutil.rmtree(jar_root)
    os.makedirs(jar_root)

    # Re-extract everything from original JAR
    out, rc = run(f"cd {jar_root} && unzip -q -o {jar_path} 2>&1")
    print(f"Full JAR extract: rc={rc}")

    # Replace BOOT-INF/classes with our compiled version
    target_boot_classes = os.path.join(jar_root, "BOOT-INF", "classes")
    if os.path.exists(target_boot_classes):
        shutil.rmtree(target_boot_classes)
    shutil.copytree(cp_classes, target_boot_classes)
    print("Replaced BOOT-INF/classes with newly compiled classes")

    # Create new JAR
    new_jar = os.path.join(work_dir, "portal-2.0.0.jar")
    if os.path.exists(new_jar):
        os.remove(new_jar)

    # Use jar command to package
    os.chdir(jar_root)
    out, rc = run("jar cf /tmp/portal-2.0.0.jar . 2>&1 | tail -10")
    print(out)
    if not os.path.exists("/tmp/portal-2.0.0.jar"):
        print("Trying zip command instead...")
        out, rc = run("zip -r /tmp/portal-2.0.0.jar . -q 2>&1 | tail -5")
        print(out)

    if os.path.exists("/tmp/portal-2.0.0.jar"):
        size = os.path.getsize("/tmp/portal-2.0.0.jar")
        print(f"New JAR: {size} bytes")

        # Copy to backend location
        shutil.copy2("/tmp/portal-2.0.0.jar", os.path.join(backend_dir, "portal-2.0.0.jar"))
        os.makedirs(os.path.join(backend_dir, "target"), exist_ok=True)
        shutil.copy2("/tmp/portal-2.0.0.jar", os.path.join(backend_dir, "target", "portal-2.0.0.jar"))
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
            print(f"Status after wait: {out}")

        print("\n=== DEPLOYMENT COMPLETE!")
    else:
        print("ERROR: Failed to create JAR")
        sys.exit(1)

if __name__ == "__main__":
    main()
