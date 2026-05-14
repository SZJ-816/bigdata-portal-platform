import pymysql
import bcrypt

db_config = {
    'host': '192.168.146.128',
    'user': 'root',
    'password': 'root123',
    'database': 'bigdata_portal',
    'port': 3306
}

users = [
    {'username': 'admin', 'password': '123456'},
    {'username': 'test', 'password': '123456'}
]

conn = None
try:
    conn = pymysql.connect(**db_config)
    cursor = conn.cursor()
    
    for user in users:
        hashed_password = bcrypt.hashpw(user['password'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        sql = "UPDATE `user` SET password = %s WHERE username = %s"
        cursor.execute(sql, (hashed_password, user['username']))
        print(f"Updated password for user: {user['username']}")
        print(f"New hash: {hashed_password}")
    
    conn.commit()
    print("\nAll passwords updated successfully!")
    
except Exception as e:
    print(f"Error: {str(e)}")
    if conn:
        conn.rollback()
finally:
    if conn:
        conn.close()