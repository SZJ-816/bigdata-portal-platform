"""
.env 文件加密/解密工具
使用 AES-256-GCM 加密，密钥通过 PBKDF2 从密码派生
用法:
  python crypto_env.py encrypt <password>   # 加密 .env -> .env.enc
  python crypto_env.py decrypt <password>   # 解密 .env.enc -> .env
"""
import os, sys, json, base64, hashlib
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.backends import default_backend

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ENV_FILE = os.path.join(BASE_DIR, '.env')
ENC_FILE = os.path.join(BASE_DIR, '.env.enc')

SALT_SIZE = 16
ITERATIONS = 600_000
NONCE_SIZE = 12


def derive_key(password: str, salt: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=ITERATIONS,
        backend=default_backend()
    )
    return kdf.derive(password.encode('utf-8'))


def encrypt(password: str):
    if not os.path.exists(ENV_FILE):
        print(f"错误: {ENV_FILE} 不存在")
        sys.exit(1)

    with open(ENV_FILE, 'r', encoding='utf-8') as f:
        plaintext = f.read()

    salt = os.urandom(SALT_SIZE)
    key = derive_key(password, salt)
    nonce = os.urandom(NONCE_SIZE)

    aesgcm = AESGCM(key)
    ciphertext = aesgcm.encrypt(nonce, plaintext.encode('utf-8'), None)

    payload = {
        'salt': base64.b64encode(salt).decode(),
        'nonce': base64.b64encode(nonce).decode(),
        'data': base64.b64encode(ciphertext).decode(),
        'iterations': ITERATIONS
    }

    with open(ENC_FILE, 'w', encoding='utf-8') as f:
        json.dump(payload, f, indent=2)

    # 验证哈希，防止密码错误
    sha = hashlib.sha256(plaintext.encode('utf-8')).hexdigest()[:8]
    print(f"已加密: {ENV_FILE} -> {ENC_FILE}")
    print(f"验证码 (sha256前8位): {sha}")
    print(f"请妥善保管你的密码，解密时需要用到")


def decrypt(password: str):
    if not os.path.exists(ENC_FILE):
        print(f"错误: {ENC_FILE} 不存在")
        sys.exit(1)

    with open(ENC_FILE, 'r', encoding='utf-8') as f:
        payload = json.load(f)

    salt = base64.b64decode(payload['salt'])
    nonce = base64.b64decode(payload['nonce'])
    ciphertext = base64.b64decode(payload['data'])
    iterations = payload.get('iterations', ITERATIONS)

    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=iterations,
        backend=default_backend()
    )
    key = kdf.derive(password.encode('utf-8'))

    aesgcm = AESGCM(key)
    try:
        plaintext = aesgcm.decrypt(nonce, ciphertext, None).decode('utf-8')
    except Exception:
        print("错误: 密码不正确或文件已损坏")
        sys.exit(1)

    with open(ENV_FILE, 'w', encoding='utf-8') as f:
        f.write(plaintext)

    sha = hashlib.sha256(plaintext.encode('utf-8')).hexdigest()[:8]
    print(f"已解密: {ENC_FILE} -> {ENV_FILE}")
    print(f"验证码: {sha}")


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print("用法:")
        print("  python crypto_env.py encrypt <password>")
        print("  python crypto_env.py decrypt <password>")
        sys.exit(1)

    action = sys.argv[1]
    password = sys.argv[2]

    if action == 'encrypt':
        encrypt(password)
    elif action == 'decrypt':
        decrypt(password)
    else:
        print(f"未知操作: {action}")
        sys.exit(1)