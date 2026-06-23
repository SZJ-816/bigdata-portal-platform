#!/usr/bin/env python3
"""
为使用频道SVG图标作为封面的新闻下载真实封面图
从原文URL抓取og:image，下载保存并更新数据库
"""
import requests
import re
import os
import hashlib
import pymysql
from urllib.parse import urljoin
from bs4 import BeautifulSoup

DB_CONFIG = {
    'host': '127.0.0.1',
    'user': 'root',
    'password': 'root123456',
    'database': 'bigdata_portal',
    'port': 3306,
    'charset': 'utf8mb4'
}

IMAGE_DIR = '/var/lib/docker/volumes/bigdata-portal-platform_news-images/_data'
THUMB_DIR = '/var/lib/docker/volumes/bigdata-portal-platform_news-images/_data/thumb'

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
}

def get_og_image(url):
    """从网页提取 og:image 或 twitter:image"""
    try:
        resp = requests.get(url, headers=HEADERS, timeout=15, allow_redirects=True)
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, 'html.parser')

        # 优先 og:image
        og = soup.find('meta', property='og:image')
        if og and og.get('content'):
            return og['content']

        # twitter:image
        tw = soup.find('meta', attrs={'name': 'twitter:image'})
        if tw and tw.get('content'):
            return tw['content']

        # 第一个 img 标签
        img = soup.find('img')
        if img and img.get('src'):
            src = img['src']
            if src.startswith('//'):
                src = 'https:' + src
            elif src.startswith('/'):
                src = urljoin(url, src)
            return src

    except Exception as e:
        print(f"  [WARN] Failed to fetch {url}: {e}")
    return None

def download_image(url):
    """下载图片，返回本地文件名"""
    try:
        resp = requests.get(url, headers=HEADERS, timeout=20, allow_redirects=True)
        resp.raise_for_status()

        content_type = resp.headers.get('Content-Type', '')
        if 'image' not in content_type and len(resp.content) < 500:
            return None

        ext = '.jpg'
        if 'png' in content_type:
            ext = '.png'
        elif 'webp' in content_type:
            ext = '.webp'

        # 用 URL 内容的 hash 作为文件名
        md5 = hashlib.md5(resp.content).hexdigest()
        filename = md5 + ext
        filepath = os.path.join(IMAGE_DIR, filename)

        if not os.path.exists(filepath):
            with open(filepath, 'wb') as f:
                f.write(resp.content)

        return filename
    except Exception as e:
        print(f"  [WARN] Failed to download {url}: {e}")
    return None

def main():
    print("=" * 60)
    print("为 SVG 封面新闻下载真实封面图")
    print("=" * 60)

    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor(pymysql.cursors.DictCursor)

    # 查找所有使用 SVG 图标的新闻
    cursor.execute("SELECT id, title, source_url, channel FROM news WHERE image_url LIKE '%channel%svg%' AND source_url IS NOT NULL AND source_url != '' ORDER BY id DESC")
    svg_news = cursor.fetchall()
    print(f"\n找到 {len(svg_news)} 条使用 SVG 图标的新闻")

    updated = 0
    failed = 0

    for i, news in enumerate(svg_news):
        news_id = news['id']
        title = news['title'][:30] if news['title'] else ''
        source_url = news['source_url']

        print(f"\n[{i+1}/{len(svg_news)}] ID={news_id} {title}...")

        # 1. 从原文提取封面图 URL
        img_url = get_og_image(source_url)
        if not img_url:
            print(f"  [SKIP] 未找到封面图")
            failed += 1
            continue

        # 处理相对 URL
        if img_url.startswith('//'):
            img_url = 'https:' + img_url
        elif img_url.startswith('/'):
            img_url = urljoin(source_url, img_url)

        # 2. 下载图片
        filename = download_image(img_url)
        if not filename:
            print(f"  [SKIP] 下载失败: {img_url[:60]}")
            failed += 1
            continue

        # 3. 更新数据库
        new_image_url = f'/images/news/{filename}'
        cursor.execute("UPDATE news SET image_url = %s WHERE id = %s", (new_image_url, news_id))
        conn.commit()
        updated += 1
        print(f"  [OK] -> {new_image_url}")

        # 限速
        if (i + 1) % 10 == 0:
            print(f"\n--- 进度: {i+1}/{len(svg_news)}, 成功: {updated}, 失败: {failed} ---\n")

    cursor.close()
    conn.close()

    print(f"\n{'=' * 60}")
    print(f"完成！成功更新: {updated}, 失败: {failed}, 总计: {len(svg_news)}")
    print(f"{'=' * 60}")

if __name__ == '__main__':
    main()
