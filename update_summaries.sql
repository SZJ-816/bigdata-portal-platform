UPDATE news 
SET summary = CONCAT('本文介绍了', 
  CASE 
    WHEN title REGEXP '^【.*】$' THEN SUBSTRING(title, 2, LENGTH(title)-2)
    WHEN title REGEXP '^\\(.*\\)$' THEN SUBSTRING(title, 2, LENGTH(title)-2)
    WHEN title REGEXP '^（.*）$' THEN SUBSTRING(title, 2, LENGTH(title)-2)
    WHEN title REGEXP '^\\[.*\\]$' THEN SUBSTRING(title, 2, LENGTH(title)-2)
    WHEN title REGEXP '^《.*》$' THEN SUBSTRING(title, 2, LENGTH(title)-2)
    ELSE title 
  END, 
  '相关内容，点击查看详情了解更多信息。')
WHERE summary IS NULL OR LENGTH(summary) < 20 OR summary LIKE '%点击查看%' OR summary LIKE '%阅读全文%';

SELECT COUNT(*) AS updated_count FROM news WHERE summary LIKE '本文介绍了%';
