import bcrypt

password = "123456"
hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
print("BCrypt hash for '123456':", hashed.decode('utf-8'))