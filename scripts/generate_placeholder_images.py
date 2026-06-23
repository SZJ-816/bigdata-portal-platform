#!/usr/bin/env python3
"""
为使用频道SVG图标作为封面的新闻生成频道主题PNG占位图 - numpy加速版
"""
import os
import pymysql
import numpy as np
from PIL import Image, ImageDraw, ImageFont

DB_CONFIG = {
    'host': '127.0.0.1',
    'user': 'root',
    'password': 'root123456',
    'database': 'bigdata_portal',
    'port': 3306,
    'charset': 'utf8mb4'
}

IMAGE_DIR = '/var/lib/docker/volumes/bigdata-portal-platform_news-images/_data'

CHANNEL_THEMES = {
    'AI':          {'c1': (102, 126, 234), 'c2': (118, 75, 162), 'label': 'AI'},
    '人工智能':     {'c1': (102, 126, 234), 'c2': (118, 75, 162), 'label': 'AI'},
    '大数据':       {'c1': (240, 147, 251), 'c2': (245, 87, 108), 'label': 'BigData'},
    '云计算':       {'c1': (79, 172, 254),  'c2': (0, 242, 254),  'label': 'Cloud'},
    '互联网':       {'c1': (67, 233, 123),  'c2': (56, 249, 215), 'label': 'Internet'},
    '硬件':         {'c1': (250, 112, 154), 'c2': (254, 225, 64), 'label': 'Hardware'},
    '创业':         {'c1': (161, 140, 209), 'c2': (251, 194, 235), 'label': 'Startup'},
    '安全':         {'c1': (252, 203, 144), 'c2': (213, 126, 235), 'label': 'Security'},
    '区块链':       {'c1': (224, 195, 252), 'c2': (142, 197, 252), 'label': 'Blockchain'},
    '数码':         {'c1': (245, 87, 108),  'c2': (255, 106, 136), 'label': 'Digital'},
    '汽车科技':     {'c1': (102, 126, 234), 'c2': (67, 233, 123),  'label': 'AutoTech'},
}

def generate_placeholder_png(channel):
    """用 numpy 快速生成渐变 PNG"""
    theme = CHANNEL_THEMES.get(channel, {'c1': (102, 126, 234), 'c2': (118, 75, 162), 'label': channel or 'News'})
    c1, c2 = np.array(theme['c1'], dtype=np.uint8), np.array(theme['c2'], dtype=np.uint8)
    label = theme['label']

    w, h = 800, 450

    # 用 numpy 向量化计算对角线渐变
    xs = np.arange(w) / w
    ys = np.arange(h) / h
    xx, yy = np.meshgrid(xs, ys)
    t = ((xx + yy) / 2).astype(np.float32)
    t = np.clip(t, 0, 1)

    # 广播插值: shape (h, w, 3)
    c1_arr = c1[np.newaxis, np.newaxis, :]
    c2_arr = c2[np.newaxis, np.newaxis, :]
    pixels = (c1_arr + t[:, :, np.newaxis] * (c2_arr - c1_arr)).astype(np.uint8)

    img = Image.fromarray(pixels, mode='RGB')
    draw = ImageDraw.Draw(img)

    try:
        font_large = ImageFont.truetype('/usr/share/fonts/dejavu/DejaVuSans-Bold.ttf', 72)
    except:
        try:
            font_large = ImageFont.truetype('/usr/share/fonts/liberation/LiberationSans-Bold.ttf', 72)
        except:
            font_large = ImageFont.load_default()

    bbox = draw.textbbox((0, 0), label, font=font_large)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = (w - tw) // 2
    y = (h - th) // 2 - 20
    draw.text((x, y), label, fill=(255, 255, 255), font=font_large)

    return img

def main():
    print("=" * 60)
    print("生成频道主题 PNG 占位图")
    print("=" * 60)

    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor(pymysql.cursors.DictCursor)

    cursor.execute("SELECT id, title, channel FROM news WHERE image_url LIKE '%channel%svg%' ORDER BY id DESC")
    svg_news = cursor.fetchall()
    print(f"\n找到 {len(svg_news)} 条使用 SVG 图标的新闻")

    updated = 0
    os.makedirs(IMAGE_DIR, exist_ok=True)

    channel_images = {}

    for i, news in enumerate(svg_news):
        news_id = news['id']
        channel = news['channel'] or '互联网'

        if channel not in channel_images:
            print(f"  生成 {channel} 占位图...")
            img = generate_placeholder_png(channel)
            filename = f"placeholder_{channel}.png"
            filepath = os.path.join(IMAGE_DIR, filename)
            img.save(filepath, 'PNG')
            channel_images[channel] = filename

        filename = channel_images[channel]
        new_image_url = f'/images/news/{filename}'
        cursor.execute("UPDATE news SET image_url = %s WHERE id = %s", (new_image_url, news_id))
        updated += 1

        if (i + 1) % 100 == 0:
            conn.commit()

    conn.commit()
    cursor.close()
    conn.close()

    print(f"\n完成！成功更新: {updated} 条")

if __name__ == '__main__':
    main()
