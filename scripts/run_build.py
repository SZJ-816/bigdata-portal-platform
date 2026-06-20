#!/usr/bin/env python3
import subprocess, os

def run(cmd, timeout=600):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    return r.stdout.decode('utf-8', errors='ignore').strip(), r.stderr.decode('utf-8', errors='ignore').strip(), r.returncode

def main():
    # 1. Clean up maven containers
    print("=== Step 1: Clean up maven containers")
    out, err, rc = run("docker ps -q --filter ancestor=maven:3.8.6-openjdk-11-slim")
    maven_ids = [x for x in out.strip().split('\n') if x]
    print(f"Running maven containers: {len(maven_ids)}")
    if len(maven_ids) > 0:
        for cid in maven_ids:
            run(f"docker kill {cid}")
            run(f"docker rm {cid} 2>/dev/null")
        print("Killed all maven containers")

    # 2. Start a fresh build
    print("\n=== Step 2: Start fresh build")
    os.chdir("/root/bigdata-portal-platform/backend")
    build_cmd = (
        "docker run --rm "
        "-v /root/bigdata-portal-platform/backend:/app "
        "-v /root/.m2:/root/.m2 "
        "-w /app "
        "maven:3.8.6-openjdk-11-slim "
        "mvn clean package -DskipTests"
    )
    print(f"Running: {build_cmd}")
    log = open("/tmp/mvn_build.log", "w")
    proc = subprocess.Popen(build_cmd, shell=True, stdout=log, stderr=subprocess.STDOUT)
    print(f"Build started with PID: {proc.pid}")

    # Wait for build to finish
    print("\n=== Step 3: Waiting for build to complete")
    import time
    waited = 0
    while True:
        if proc.poll() is not None:
            break
        time.sleep(10)
        waited += 10
        if waited % 60 == 0:
            print(f"  ...waiting {waited}s, checking...")
            try:
                with open("/tmp/mvn_build.log", "r") as f:
                    lines = f.readlines()
                if lines:
                    print(f"  ...last 5 lines:")
                    for line in lines[-5:]:
                        print(f"    {line.strip()}")
            except:
                pass
        if waited > 1800:  # 30 min timeout
            print("  ...timeout reached, killing")
            proc.kill()
            break

    rc = proc.returncode
    print(f"\n=== Build finished with code: {rc}")

    # 3. Check if jar was built
    jar_path = "/root/bigdata-portal-platform/backend/target/portal-2.0.0.jar"
    if os.path.exists(jar_path):
        stat = os.stat(jar_path)
        print(f"JAR exists, size: {stat.st_size} bytes")
        # Copy to backend root
        subprocess.Popen("cp /root/bigdata-portal-platform/backend/target/portal-2.0.0.jar /root/bigdata-portal-platform/backend/portal-2.0.0.jar", shell=True).wait()
        print("JAR copied to backend/")

        # 4. Rebuild docker image
        print("\n=== Step 4: Rebuild backend docker image")
        os.chdir("/root/bigdata-portal-platform")
        subprocess.Popen("docker compose build backend", shell=True).wait()
        print("Docker image rebuilt")

        # 5. Restart backend
        print("\n=== Step 5: Restart backend")
        subprocess.Popen("docker compose up -d backend", shell=True).wait()
        print("Backend restarted")

        print("\n=== ALL DONE!")
    else:
        print(f"ERROR: JAR not found at {jar_path}")
        with open("/tmp/mvn_build.log", "r") as f:
            print("Last 50 lines of build log:")
            for line in f.readlines()[-50:]:
                print(line.strip())

if __name__ == "__main__":
    main()
