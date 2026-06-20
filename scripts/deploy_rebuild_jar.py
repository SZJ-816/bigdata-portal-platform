#!/usr/bin/env python3
"""Rebuild JAR with proper Spring Boot manifest"""
import subprocess, os, sys, shutil, time

def run(cmd, timeout=600):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    return (r.stdout.decode('utf-8', errors='ignore').strip() + 
            (r.stderr.decode('utf-8', errors='ignore').strip() if r.stderr else '')), r.returncode

def main():
    base = "/root/bigdata-portal-platform"
    backend_dir = os.path.join(base, "backend")
    work_dir = "/tmp/java_build4"

    # Check if we have the compiled classes from the previous run
    classes_dir = os.path.join(work_dir, "classes")
    jar_root = os.path.join(work_dir, "jar_root")

    if not os.path.exists(classes_dir):
        print("ERROR: No compiled classes directory. Need to re-run compilation.")
        sys.exit(1)

    # Check if we still have libs extracted
    lib_dir = os.path.join(work_dir, "libs", "BOOT-INF", "lib")
    if not os.path.exists(lib_dir):
        print("ERROR: No libs extracted.")
        sys.exit(1)

    class_count = 0
    for r, d, fs in os.walk(classes_dir):
        for f2 in fs:
            if f2.endswith(".class"):
                class_count += 1
    print(f"Classes available: {class_count}")

    print("\n=== Rebuild JAR with Spring Boot manifest ===")

    # Create the jar_root structure: same as before but with proper manifest
    rebuild_dir = "/tmp/jar_rebuild"
    if os.path.exists(rebuild_dir):
        shutil.rmtree(rebuild_dir)
    os.makedirs(rebuild_dir)

    # Create BOOT-INF structure
    os.makedirs(os.path.join(rebuild_dir, "BOOT-INF", "classes"))
    os.makedirs(os.path.join(rebuild_dir, "BOOT-INF", "lib"))
    os.makedirs(os.path.join(rebuild_dir, "META-INF"))

    # Copy classes
    shutil.copytree(classes_dir, os.path.join(rebuild_dir, "BOOT-INF", "classes"), dirs_exist_ok=True)
    print("Copied classes")

    # Copy libs
    for f in os.listdir(lib_dir):
        if f.endswith(".jar"):
            shutil.copy2(os.path.join(lib_dir, f), os.path.join(rebuild_dir, "BOOT-INF", "lib", f))
    lib_count = len([f for f in os.listdir(os.path.join(rebuild_dir, "BOOT-INF", "lib")) if f.endswith(".jar")])
    print(f"Copied {lib_count} lib JARs")

    # Also need org.springframework.boot.loader classes in root
    # Spring Boot JARs have the loader classes at the root level (BOOT-INF is separate)
    # These come from spring-boot-loader - we need to extract these
    # Actually they're in the root of the JAR, not in BOOT-INF
    spring_boot_loader = None
    for f in os.listdir(os.path.join(rebuild_dir, "BOOT-INF", "lib")):
        if "spring-boot-loader" in f:
            spring_boot_loader = os.path.join(rebuild_dir, "BOOT-INF", "lib", f)
            print(f"Found spring-boot-loader: {f}")
            break

    if spring_boot_loader is None:
        # Check if we have spring-boot jar
        for f in os.listdir(os.path.join(rebuild_dir, "BOOT-INF", "lib")):
            if f.startswith("spring-boot-") and "loader" not in f and "autoconfigure" not in f and "actuator" not in f and "starter" not in f:
                spring_boot_loader = os.path.join(rebuild_dir, "BOOT-INF", "lib", f)
                print(f"Using {f} (may not have loader)")
                break

    # The JarLauncher classes need to be at the ROOT of the JAR, not in BOOT-INF
    # Spring Boot executable JAR structure:
    # /org/springframework/boot/loader/ (JarLauncher etc)
    # /BOOT-INF/classes/ (our classes)
    # /BOOT-INF/lib/ (dependencies)
    # /META-INF/MANIFEST.MF

    # Let me check - maybe the original jar_root from previous build still has the loader classes
    old_jar_root = os.path.join(work_dir, "jar_root")
    if os.path.exists(old_jar_root):
        # Copy everything from old jar_root except BOOT-INF/classes (which we've replaced)
        for entry in os.listdir(old_jar_root):
            src = os.path.join(old_jar_root, entry)
            dst = os.path.join(rebuild_dir, entry)
            if entry == "BOOT-INF":
                # Keep lib, but replace classes
                boot_src = os.path.join(src, "lib")
                boot_dst = os.path.join(dst, "lib")
                if os.path.exists(boot_src):
                    if os.path.exists(boot_dst):
                        shutil.rmtree(boot_dst)
                    shutil.copytree(boot_src, boot_dst)
                # classes already copied
            elif entry != "META-INF":
                # org/, etc. - copy these (spring boot loader)
                if os.path.isdir(src):
                    shutil.copytree(src, dst, dirs_exist_ok=True)
                else:
                    shutil.copy2(src, dst)
        print("Copied spring boot loader structure from original JAR")

    # Create manifest
    manifest_path = os.path.join(rebuild_dir, "META-INF", "MANIFEST.MF")
    with open(manifest_path, "w") as f:
        f.write("Manifest-Version: 1.0\n")
        f.write("Spring-Boot-Version: 2.7.0\n")
        f.write("Main-Class: org.springframework.boot.loader.JarLauncher\n")
        f.write("Start-Class: com.bigdata.portal.PortalApplication\n")
        f.write("Spring-Boot-Classes: BOOT-INF/classes/\n")
        f.write("Spring-Boot-Lib: BOOT-INF/lib/\n")
        f.write("\n")
    print(f"Created manifest")

    # Verify we have the loader classes
    loader_path = os.path.join(rebuild_dir, "org", "springframework", "boot", "loader")
    if os.path.exists(loader_path):
        loader_classes = []
        for r, d, fs in os.walk(loader_path):
            for f2 in fs:
                if f2.endswith(".class"):
                    loader_classes.append(f2)
        print(f"Found {len(loader_classes)} Spring Boot loader classes")
    else:
        print(f"WARNING: No Spring Boot loader classes found at {loader_path}")

    # Create JAR using jar command with manifest
    new_jar = "/tmp/portal-2.0.0.jar"
    if os.path.exists(new_jar):
        os.remove(new_jar)

    # cd into rebuild_dir and use jar cfm to include manifest
    os.chdir(rebuild_dir)
    # jar cfm output.jar manifest.txt files...
    cmd = "jar cfm /tmp/portal-2.0.0.jar META-INF/MANIFEST.MF . 2>&1"
    out, rc = run(cmd)
    print(f"jar command rc={rc}")
    if out:
        print(out[:500])

    if not os.path.exists(new_jar) or os.path.getsize(new_jar) < 1000000:
        # Try with zip
        print("Trying zip command...")
        out, rc = run("zip -r /tmp/portal-2.0.0.jar . -q 2>&1")
        print(f"zip rc={rc}")

    size = os.path.getsize(new_jar) if os.path.exists(new_jar) else 0
    print(f"JAR size: {size} bytes")

    # Verify manifest
    out, rc = run(f"unzip -p /tmp/portal-2.0.0.jar META-INF/MANIFEST.MF")
    print(f"Manifest in new JAR:\n{out}")

    # Copy to backend location
    shutil.copy2(new_jar, os.path.join(backend_dir, "portal-2.0.0.jar"))
    shutil.copy2(new_jar, os.path.join(backend_dir, "target", "portal-2.0.0.jar"))
    print("JAR copied")

    # Rebuild Docker image and restart
    print("\n=== Rebuild backend Docker image ===")
    os.chdir(base)
    out, rc = run("docker compose build backend 2>&1 | tail -10")
    print(out)

    print("\n=== Restart backend ===")
    out, rc = run("docker compose up -d backend 2>&1 | tail -10")
    print(out)

    # Wait for health
    print("\n=== Health check ===")
    for i in range(50):
        out, rc = run("docker inspect -f '{{.State.Health.Status}}' bigdata-backend 2>&1")
        status = out.strip()
        if i % 5 == 0 or "healthy" in status or "unhealthy" in status:
            print(f"  t={i*5}s status={status}")
        if "healthy" in status:
            print(f"Backend healthy after {i*5}s")
            break
        time.sleep(5)
    else:
        print(f"Final status: {out}")

    print("\n=== DONE! ===")

if __name__ == "__main__":
    main()
