#!/usr/bin/env python3
"""Rebuild JAR with proper Spring Boot manifest - Python 3.6 compatible"""
import subprocess, os, sys, shutil, time

def run(cmd, timeout=600):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    return (r.stdout.decode('utf-8', errors='ignore').strip() + 
            (r.stderr.decode('utf-8', errors='ignore').strip() if r.stderr else '')), r.returncode

def copy_tree(src, dst):
    """Copy tree, creating dst if needed, merging if exists"""
    if not os.path.exists(dst):
        shutil.copytree(src, dst)
        return
    for root, dirs, files in os.walk(src):
        rel = os.path.relpath(root, src)
        target_dir = os.path.join(dst, rel)
        if not os.path.exists(target_dir):
            os.makedirs(target_dir)
        for f in files:
            shutil.copy2(os.path.join(root, f), os.path.join(target_dir, f))

def main():
    base = "/root/bigdata-portal-platform"
    backend_dir = os.path.join(base, "backend")
    work_dir = "/tmp/java_build4"
    classes_dir = os.path.join(work_dir, "classes")
    old_jar_root = os.path.join(work_dir, "jar_root")

    if not os.path.exists(classes_dir):
        print("ERROR: No compiled classes. Re-run compilation first.")
        sys.exit(1)

    if not os.path.exists(old_jar_root):
        print("ERROR: No original JAR extract.")
        sys.exit(1)

    # Count classes
    class_count = sum(1 for r, d, fs in os.walk(classes_dir) for f2 in fs if f2.endswith(".class"))
    print(f"Classes: {class_count}")

    print("\n=== Rebuild JAR ===")
    rebuild_dir = "/tmp/jar_rebuild2"
    if os.path.exists(rebuild_dir):
        shutil.rmtree(rebuild_dir)
    os.makedirs(rebuild_dir)

    # Copy EVERYTHING from old jar_root first (keeps loader, libs, etc)
    copy_tree(old_jar_root, rebuild_dir)
    print(f"Copied original structure")

    # Now REPLACE BOOT-INF/classes with our freshly compiled classes
    boot_classes = os.path.join(rebuild_dir, "BOOT-INF", "classes")
    if os.path.exists(boot_classes):
        shutil.rmtree(boot_classes)
    copy_tree(classes_dir, boot_classes)
    print("Replaced BOOT-INF/classes")

    # Create proper Spring Boot manifest
    meta_inf = os.path.join(rebuild_dir, "META-INF")
    if not os.path.exists(meta_inf):
        os.makedirs(meta_inf)

    manifest_path = os.path.join(meta_inf, "MANIFEST.MF")
    with open(manifest_path, "w") as f:
        f.write("Manifest-Version: 1.0\n")
        f.write("Spring-Boot-Version: 2.7.0\n")
        f.write("Main-Class: org.springframework.boot.loader.JarLauncher\n")
        f.write("Start-Class: com.bigdata.portal.PortalApplication\n")
        f.write("Spring-Boot-Classes: BOOT-INF/classes/\n")
        f.write("Spring-Boot-Lib: BOOT-INF/lib/\n")
        f.write("\n")
    print("Wrote manifest")

    # Verify structure
    loader = os.path.join(rebuild_dir, "org", "springframework", "boot", "loader")
    if os.path.exists(loader):
        loader_count = sum(1 for r, d, fs in os.walk(loader) for f2 in fs if f2.endswith(".class"))
        print(f"Spring Boot loader classes: {loader_count}")
    else:
        print("WARNING: No loader classes! Checking what's at root...")
        for item in os.listdir(rebuild_dir)[:20]:
            print(f"  {item}")

    # Check if there's org/springframework/boot/loader/ somewhere
    # Sometimes it's in BOOT-INF/lib/spring-boot-loader.jar
    loader_jar = None
    lib_dir = os.path.join(rebuild_dir, "BOOT-INF", "lib")
    if os.path.exists(lib_dir):
        for f in os.listdir(lib_dir):
            if "spring-boot-loader" in f:
                loader_jar = os.path.join(lib_dir, f)
                print(f"Found loader JAR: {f}")
                break

    if not os.path.exists(loader) and loader_jar:
        # Extract spring-boot-loader classes to root of JAR
        extract_dir = "/tmp/loader_extract"
        if os.path.exists(extract_dir):
            shutil.rmtree(extract_dir)
        os.makedirs(extract_dir)
        out, rc = run(f"cd {extract_dir} && unzip -q {loader_jar} 2>&1")
        print(f"Extracted loader: {rc}")
        # Copy org/ to rebuild root
        loader_org = os.path.join(extract_dir, "org")
        if os.path.exists(loader_org):
            copy_tree(loader_org, os.path.join(rebuild_dir, "org"))
            print("Copied loader classes to JAR root")

    # Package JAR
    new_jar = "/tmp/portal-2.0.0.jar"
    if os.path.exists(new_jar):
        os.remove(new_jar)

    os.chdir(rebuild_dir)
    # Use jar with manifest - important: manifest must be first argument after options
    cmd = "jar cfm /tmp/portal-2.0.0.jar META-INF/MANIFEST.MF . 2>&1"
    out, rc = run(cmd)
    print(f"jar rc={rc}")
    if out:
        print(out[:300])

    if not os.path.exists(new_jar) or os.path.getsize(new_jar) < 1000000:
        print("Jar failed, trying zip...")
        out, rc = run("zip -r /tmp/portal-2.0.0.jar . -q 2>&1")
        print(f"zip rc={rc}")

    size = os.path.getsize(new_jar) if os.path.exists(new_jar) else 0
    print(f"Final JAR size: {size} bytes")

    # Verify manifest
    out, rc = run(f"unzip -p /tmp/portal-2.0.0.jar META-INF/MANIFEST.MF 2>&1")
    print(f"Manifest:\n{out}")

    # Deploy
    shutil.copy2(new_jar, os.path.join(backend_dir, "portal-2.0.0.jar"))
    shutil.copy2(new_jar, os.path.join(backend_dir, "target", "portal-2.0.0.jar"))
    print("JAR deployed")

    print("\n=== Rebuild Docker image ===")
    os.chdir(base)
    out, rc = run("docker compose build backend 2>&1 | tail -10")
    print(out)

    print("\n=== Restart backend ===")
    out, rc = run("docker compose up -d backend 2>&1 | tail -10")
    print(out)

    print("\n=== Health check ===")
    for i in range(50):
        out, rc = run("docker inspect -f '{{.State.Health.Status}}' bigdata-backend 2>&1")
        status = out.strip()
        if i % 5 == 0 or "healthy" in status or "unhealthy" in status:
            print(f"  t={i*5}s status={status}")
        if "healthy" in status:
            print(f"HEALTHY after {i*5}s!")
            break
        time.sleep(5)
    else:
        print(f"Final: {out}")
        # Show logs
        out, rc = run("docker logs bigdata-backend 2>&1 | tail -20")
        print(f"Logs:\n{out}")

    print("\n=== DONE ===")

if __name__ == "__main__":
    main()
