#!/usr/bin/env python3
import subprocess, os

def run(cmd, check=False):
    r = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=600)
    return r.stdout.strip(), r.stderr.strip(), r.returncode

def main():
    # 1. Check current status
    print("=== Step 1: Check maven build status")
    # Kill all maven containers
    out, err, rc = run("docker ps -q --filter ancestor=maven:3.8.6-openjdk-11-slim")
    maven_ids = out.strip().split('\n') if out.strip() else []
    print(f"Running maven containers: {len(maven_ids)}")
    if len(maven_ids) > 1:
        # Kill all except first
        for cid in maven_ids[1:]:
            run(f"docker kill {cid}")
            run(f"docker rm {cid}")
        print(f"Killed duplicates")

    # 2. Start a clean maven build
    print("\n=== Step 2: Start clean maven build")
    out, err, rc = run("docker ps -q --filter ancestor=maven:3.8.6-openjdk-11-slim")
    if not out.strip():
        print("Starting maven build...")
        subprocess.Popen(
            "docker run --rm -v /root/bigdata-portal-platform/backend:/app " + 
            "-v /root/.m2:/root/.m2 -w /app maven:3.8.6-openjdk-11-slim " + 
            "mvn clean package -DskipTests > /tmp/mvn_build.log 2>&1",
            shell=True)
        print("Build started, will check again later")
    else:
        print("Maven build already running")

    print("\n=== Done for now")

if __name__ == "__main__":
    main()
