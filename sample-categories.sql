-- Sample SQL script to create initial categories
-- Run this after the migrations have been applied

-- Insert common categories
INSERT INTO categories (name, display_name, description, icon, sort_order) VALUES
('art-design', 'Art & Design', 'Artistic and design-focused trends', '🎨', 1),
('photography', 'Photography', 'Photography and photo editing trends', '📷', 2),
('fashion', 'Fashion', 'Fashion and style trends', '👗', 3),
('nature', 'Nature', 'Nature and landscape trends', '🌿', 4),
('portrait', 'Portrait', 'Portrait and people photography', '👤', 5),
('abstract', 'Abstract', 'Abstract and artistic expressions', '🎭', 6),
('ai-tools', 'AI Tools', 'AI-powered editing and generation tools', '🤖', 7),
('effects', 'Effects', 'Special effects and filters', '✨', 8),
('vintage', 'Vintage', 'Retro and vintage styles', '📻', 9),
('modern', 'Modern', 'Modern and contemporary styles', '🔮', 10);

-- Example: Assign multiple categories to a trend
-- Replace {trend_id} with actual trend IDs
-- INSERT INTO trend_categories (trend_id, category_id) VALUES
-- ({trend_id}, (SELECT id FROM categories WHERE name = 'art-design')),
-- ({trend_id}, (SELECT id FROM categories WHERE name = 'effects'));

-- Query to see trends with their categories
-- SELECT 
--     t.id,
--     t.trend_name,
--     GROUP_CONCAT(c.name) as categories
-- FROM trends t
-- LEFT JOIN trend_categories tc ON t.id = tc.trend_id
-- LEFT JOIN categories c ON tc.category_id = c.id
-- GROUP BY t.id, t.trend_name;
