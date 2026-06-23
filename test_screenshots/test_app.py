from playwright.sync_api import sync_playwright
import os

SCREENSHOT_DIR = r"d:\workspace\bigdata-portal-platform\test_screenshots"
os.makedirs(SCREENSHOT_DIR, exist_ok=True)

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page(viewport={"width": 1920, "height": 1080})

    # ========== Step 1: Homepage ==========
    print("=== Step 1: Loading homepage ===")
    try:
        page.goto('http://localhost:3000', timeout=15000)
        page.wait_for_load_state('networkidle', timeout=15000)
        page.screenshot(path=os.path.join(SCREENSHOT_DIR, '01_homepage.png'), full_page=True)
        print("Homepage screenshot saved.")
    except Exception as e:
        print(f"Homepage error: {e}")
        page.screenshot(path=os.path.join(SCREENSHOT_DIR, '01_homepage_error.png'))

    # ========== Step 2: Click on AI channel ==========
    print("\n=== Step 2: Clicking AI channel ===")
    try:
        # Try to find a channel link - look for AI or similar text
        ai_link = page.locator('text=AI').first
        if ai_link.is_visible(timeout=5000):
            ai_link.click()
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '02_ai_channel.png'), full_page=True)
            print("AI channel screenshot saved.")
        else:
            print("AI link not visible, trying alternative selectors...")
            # Try nav links
            nav_links = page.locator('nav a, .nav a, .menu a, [class*="channel"] a, [class*="category"] a')
            count = nav_links.count()
            print(f"Found {count} nav links")
            for i in range(min(count, 10)):
                text = nav_links.nth(i).text_content()
                print(f"  Link {i}: {text}")
            # Click the first available channel link that looks like a category
            if count > 0:
                nav_links.first.click()
                page.wait_for_load_state('networkidle', timeout=10000)
                page.wait_for_timeout(2000)
                page.screenshot(path=os.path.join(SCREENSHOT_DIR, '02_channel.png'), full_page=True)
                print("Channel screenshot saved.")
    except Exception as e:
        print(f"AI channel error: {e}")
        page.screenshot(path=os.path.join(SCREENSHOT_DIR, '02_ai_channel_error.png'))

    # ========== Step 3: Click on a news article ==========
    print("\n=== Step 3: Clicking news article ===")
    try:
        # Look for article links/cards
        article = page.locator('article a, .article a, .news-item a, .card a, [class*="article"] a, [class*="news"] a, [class*="item"] a').first
        if article.is_visible(timeout=5000):
            article.click()
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '03_article_detail.png'), full_page=True)
            print("Article detail screenshot saved.")
        else:
            print("Article link not found with primary selectors, trying broader search...")
            # Try any link that looks like an article
            all_links = page.locator('a')
            count = all_links.count()
            print(f"Found {count} total links on page")
            # Try clicking a link that's not a nav link
            for i in range(count):
                link = all_links.nth(i)
                href = link.get_attribute('href') or ''
                text = link.text_content() or ''
                if '/article' in href or '/news' in href or '/detail' in href:
                    link.click()
                    page.wait_for_load_state('networkidle', timeout=10000)
                    page.wait_for_timeout(2000)
                    page.screenshot(path=os.path.join(SCREENSHOT_DIR, '03_article_detail.png'), full_page=True)
                    print(f"Article detail screenshot saved (clicked: {href}, text: {text[:50]}).")
                    break
            else:
                print("No article link found")
                page.screenshot(path=os.path.join(SCREENSHOT_DIR, '03_no_article.png'), full_page=True)
    except Exception as e:
        print(f"Article detail error: {e}")
        page.screenshot(path=os.path.join(SCREENSHOT_DIR, '03_article_error.png'))

    # ========== Step 4: Search page ==========
    print("\n=== Step 4: Search page ===")
    try:
        page.goto('http://localhost:3000', timeout=15000)
        page.wait_for_load_state('networkidle', timeout=15000)
        
        # Look for search icon/input/link
        search_input = page.locator('input[type="search"], input[placeholder*="搜索"], input[placeholder*="search"], input[placeholder*="Search"], [class*="search"] input')
        search_link = page.locator('a[href*="search"], [class*="search"] a, button[class*="search"]')
        
        if search_input.count() > 0:
            search_input.first.click()
            search_input.first.fill('AI')
            search_input.first.press('Enter')
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '04_search.png'), full_page=True)
            print("Search screenshot saved (via input).")
        elif search_link.count() > 0:
            search_link.first.click()
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '04_search.png'), full_page=True)
            print("Search screenshot saved (via link).")
        else:
            # Try navigating directly to search page
            print("No search element found, trying direct URL...")
            page.goto('http://localhost:3000/search', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '04_search_direct.png'), full_page=True)
            print("Search page screenshot saved (direct URL).")
    except Exception as e:
        print(f"Search page error: {e}")
        page.screenshot(path=os.path.join(SCREENSHOT_DIR, '04_search_error.png'))

    # ========== Step 5: Login page ==========
    print("\n=== Step 5: Login page ===")
    try:
        # Try to find login link on the page first
        page.goto('http://localhost:3000', timeout=15000)
        page.wait_for_load_state('networkidle', timeout=15000)
        
        login_link = page.locator('a[href*="login"], button:has-text("登录"), a:has-text("登录"), a:has-text("Login"), button:has-text("Login"), [class*="login"] a')
        
        if login_link.count() > 0:
            login_link.first.click()
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '05_login.png'), full_page=True)
            print("Login page screenshot saved (via link).")
        else:
            # Try direct URL
            print("No login link found, trying direct URL...")
            page.goto('http://localhost:3000/login', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '05_login_direct.png'), full_page=True)
            print("Login page screenshot saved (direct URL).")
    except Exception as e:
        print(f"Login page error: {e}")
        try:
            page.goto('http://localhost:3000/login', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '05_login_fallback.png'), full_page=True)
        except Exception as e2:
            print(f"Login page fallback error: {e2}")

    # ========== Step 6: Bigscreen dashboard ==========
    print("\n=== Step 6: Bigscreen dashboard ===")
    try:
        page.goto('http://localhost:3001/bigscreen/', timeout=15000)
        page.wait_for_load_state('networkidle', timeout=15000)
        page.wait_for_timeout(3000)  # Extra wait for dashboard animations
        page.screenshot(path=os.path.join(SCREENSHOT_DIR, '06_bigscreen.png'), full_page=True)
        print("Bigscreen dashboard screenshot saved.")
    except Exception as e:
        print(f"Bigscreen dashboard error: {e}")
        try:
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, '06_bigscreen_error.png'))
        except:
            pass

    # ========== Collect page info for reporting ==========
    print("\n=== Page Structure Analysis ===")
    try:
        page.goto('http://localhost:3000', timeout=15000)
        page.wait_for_load_state('networkidle', timeout=15000)
        
        # Get page title
        title = page.title()
        print(f"Page title: {title}")
        
        # Get all visible links
        links = page.locator('a[href]')
        link_count = links.count()
        print(f"\nTotal links: {link_count}")
        for i in range(min(link_count, 30)):
            link = links.nth(i)
            href = link.get_attribute('href') or ''
            text = (link.text_content() or '').strip()[:50]
            print(f"  [{i}] {text} -> {href}")
            
        # Get all buttons
        buttons = page.locator('button')
        btn_count = buttons.count()
        print(f"\nTotal buttons: {btn_count}")
        for i in range(min(btn_count, 20)):
            text = (buttons.nth(i).text_content() or '').strip()[:50]
            print(f"  [{i}] {text}")
            
        # Get all inputs
        inputs = page.locator('input')
        input_count = inputs.count()
        print(f"\nTotal inputs: {input_count}")
        for i in range(min(input_count, 10)):
            inp = inputs.nth(i)
            itype = inp.get_attribute('type') or 'text'
            placeholder = inp.get_attribute('placeholder') or ''
            print(f"  [{i}] type={itype}, placeholder={placeholder}")
            
        # Check for nav/menu
        nav = page.locator('nav, [class*="nav"], [class*="menu"], [class*="header"]')
        print(f"\nNav elements: {nav.count()}")
        for i in range(min(nav.count(), 5)):
            print(f"  [{i}] {nav.nth(i).text_content()[:200]}")
            
    except Exception as e:
        print(f"Page analysis error: {e}")

    browser.close()
    print("\n=== Testing complete ===")
