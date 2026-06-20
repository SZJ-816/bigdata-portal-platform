#!/usr/bin/env python3
"""Compile the modified Java files using the Flink JDK, rebuild JAR, and deploy"""
import subprocess, os, sys

def run(cmd, timeout=300):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    return r.stdout.decode('utf-8', errors='ignore').strip(), r.stderr.decode('utf-8', errors='ignore').strip(), r.returncode

def main():
    base = "/root/bigdata-portal-platform"
    backend_dir = os.path.join(base, "backend")
    src_dir = os.path.join(backend_dir, "src/main/java/com/bigdata/portal")
    work_dir = "/tmp/java_build"

    # 1. Check javac availability
    print("=== Step 1: Check compilation environment")
    out, err, rc = run("docker run --rm flink:1.17.1-java8 javac -version 2>&1")
    print(f"Flink javac: {out} {err}")

    # 2. Create workspace
    print("\n=== Step 2: Prepare build workspace")
    os.makedirs(work_dir, exist_ok=True)
    os.makedirs(os.path.join(work_dir, "src"), exist_ok=True)
    os.makedirs(os.path.join(work_dir, "classes"), exist_ok=True)

    # 3. Copy Java files to build
    print("\n=== Step 3: Collect Java source files")
    import shutil
    for root, dirs, files in os.walk(src_dir):
        for f in files:
            if f.endswith(".java"):
                src_path = os.path.join(root, f)
                rel = os.path.relpath(src_path, src_dir)
                dst = os.path.join(work_dir, "src", rel)
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                shutil.copy2(src_path, dst)
    # Count
    total = sum(1 for r, d, fs in os.walk(os.path.join(work_dir, "src")) for f in fs if f.endswith(".java"))
    print(f"Total Java files: {total}")

    # 4. Extract Spring dependencies from existing JAR
    print("\n=== Step 4: Extract dependencies for classpath")
    jar_path = os.path.join(backend_dir, "portal-2.0.0.jar")
    dep_dir = os.path.join(work_dir, "deps")
    os.makedirs(dep_dir, exist_ok=True)
    os.chdir(dep_dir)
    run(f"unzip -q -o {jar_path} 'BOOT-INF/lib/*' -d {work_dir}/jar_deps 2>/dev/null")
    run(f"unzip -q -o {jar_path} 'BOOT-INF/classes/*' -d {work_dir}/jar_classes 2>/dev/null")
    print("Dependencies extracted")

    # 5. Build classpath
    cp_dirs = [os.path.join(work_dir, "jar_deps", "BOOT-INF", "lib"), 
               os.path.join(work_dir, "jar_classes", "BOOT-INF", "classes")]
    cp = ":".join(cp_dirs)

    # 6. Find all .java files
    java_files = []
    java_src_root = os.path.join(work_dir, "src")
    for root, dirs, files in os.walk(java_src_root):
        for f in files:
            if f.endswith(".java"):
                java_files.append(os.path.join(root, f))

    print(f"Compiling {len(java_files)} Java files")

    # Write file list
    filelist_path = os.path.join(work_dir, "files.txt")
    with open(filelist_path, "w") as f:
        for jf in java_files:
            f.write(jf + "\n")

    # 7. Compile using Flink container's javac
    print("\n=== Step 5: Compile")
    # We need to mount the directories into the container
    compile_cmd = (
        f"docker run --rm "
        f"-v {work_dir}:/build "
        f"flink:1.17.1-java8 "
        f"javac -encoding UTF-8 -source 1.8 -target 1.8 "
        f"-cp '/build/jar_deps/BOOT-INF/lib/*:/build/jar_classes/BOOT-INF/classes' "
        f"-d /build/classes "
        f"@/build/files.txt 2>&1"
    )
    # Write compile command file
    with open(os.path.join(work_dir, "compile.sh"), "w") as f:
        f.write("#!/bin/bash\n")
        f.write("cd /build\n")
        f.write("javac -encoding UTF-8 -source 1.8 -target 1.8 -cp '/build/jar_deps/BOOT-INF/lib/*:/build/jar_classes/BOOT-INF/classes' -d /build/classes @/build/files.txt 2>&1\n")
        f.write("echo \"EXIT_CODE:$?\"\n")

    # Run the compilation in docker
    out, err, rc = run(f"chmod +x {work_dir}/compile.sh && "
                        f"docker run --rm -v {work_dir}:/build flink:1.17.1-java8 bash /build/compile.sh 2>&1 | tail -50")
    print(out)
    if err:
        print(f"stderr: {err}")

    # 8. Check if compilation succeeded
    class_count = sum(1 for r, d, fs in os.walk(os.path.join(work_dir, "classes")) for f in fs if f.endswith(".class"))
    print(f"Generated {class_count} .class files")

    if class_count == 0:
        print("ERROR: No class files generated!")
        sys.exit(1)

    # 9. Copy new classes into the JAR structure
    print("\n=== Step 6: Update JAR with new classes")
    jar_classes_root = os.path.join(work_dir, "jar_classes", "BOOT-INF", "classes")
    # Copy all generated classes to jar_classes/BOOT-INF/classes
    for root, dirs, files in os.walk(os.path.join(work_dir, "classes")):
        for f in files:
            src = os.path.join(root, f)
            rel = os.path.relpath(src, os.path.join(work_dir, "classes"))
            dst = os.path.join(jar_classes_root, rel)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copy2(src, dst)
    print("Classes copied")

    # 10. Repack JAR
    print("\n=== Step 7: Repackage JAR")
    os.chdir(os.path.join(work_dir, "jar_classes"))
    out, err, rc = run("zip -r /tmp/portal-2.0.0.jar . -q 2>&1 | tail -5")
    print(out)
    # Check if zip worked
    if not os.path.exists("/tmp/portal-2.0.0.jar"):
        # Try jar command
        out, err, rc = run(f"docker run --rm -v {work_dir}/jar_classes:/jardir flink:1.17.1-java8 bash -c 'cd /jardir && jar cf /tmp/portal-2.0.0.jar .' 2>&1; cp {work_dir}/jar_classes/portal-2.0.0.jar /tmp/ 2>/dev/null || docker cp $(docker ps -ql):/tmp/portal-2.0.0.jar /tmp/ 2>/dev/null")
        print(err)
        print(out)

    # Check final jar
    if os.path.exists("/tmp/portal-2.0.0.jar"):
        new_jar = "/tmp/portal-2.0.0.jar"
        size = os.path.getsize(new_jar)
        print(f"New JAR size: {size} bytes")

        # 11. Copy to backend dir
        shutil.copy2(new_jar, os.path.join(backend_dir, "portal-2.0.0.jar"))
        shutil.copy2(new_jar, os.path.join(backend_dir, "target", "portal-2.0.0.jar"))
        print("JAR copied to backend/")

        # 12. Rebuild docker image
        print("\n=== Step 8: Rebuild backend Docker image")
        os.chdir(base)
        out, err, rc = run("docker compose build backend 2>&1 | tail -20")
        print(out)
        if err: print(err)

        # 13. Restart backend
        print("\n=== Step 9: Restart backend")
        out, err, rc = run("docker compose up -d backend 2>&1 | tail -10")
        print(out)

        # 14. Wait for healthy
        print("\n=== Step 10: Wait for health")
        import time
        for i in range(30):
            out, err, rc = run("docker inspect -f '{{.State.Health.Status}}' bigdata-backend 2>&1")
            if "healthy" in out:
                print(f"Backend healthy after {i*5}s")
                break
            time.sleep(5)

        print("\n=== DEPLOYMENT COMPLETE!")
    else:
        print("ERROR: Failed to create new JAR")
        sys.exit(1)

if __name__ == "__main__":
    main()
