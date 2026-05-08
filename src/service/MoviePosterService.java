package service;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class MoviePosterService {
    private static final Map<String, ImageIcon> posterCache = new HashMap<>();
    private static final String POSTERS_DIR = "src/resources/posters/";

    public static ImageIcon getMoviePoster(String movieTitle, int width, int height) {
        String cacheKey = movieTitle + "_" + width + "_" + height;

        if (posterCache.containsKey(cacheKey)) {
            return posterCache.get(cacheKey);
        }

        // Try to load local poster
        ImageIcon poster = loadLocalPoster(movieTitle, width, height);

        // If no local poster, create gradient poster
        if (poster == null) {
            poster = createGradientPoster(movieTitle, width, height);
        }

        posterCache.put(cacheKey, poster);
        return poster;
    }

    private static ImageIcon loadLocalPoster(String movieTitle, int width, int height) {
        // Convert movie title to filename
        String filename = movieTitle.toLowerCase()
                .replace(" ", "_")
                .replace(":", "")
                .replace("'", "")
                .replace("-", "_") + ".jpg";

        // Try multiple paths
        String[] paths = {
                POSTERS_DIR + filename,
                "src/resources/posters/" + filename,
                "resources/posters/" + filename,
                filename
        };

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    Image originalImage = ImageIO.read(file);
                    if (originalImage != null) {
                        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.out.println("Could not load: " + path);
                }
            }
        }
        return null;
    }

    private static ImageIcon createGradientPoster(String movieTitle, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int hash = Math.abs(movieTitle.hashCode());
        Color color1 = new Color(30 + (hash % 40), 50 + (hash % 60), 80 + (hash % 70));
        Color color2 = new Color(10 + (hash % 30), 20 + (hash % 40), 30 + (hash % 50));

        GradientPaint gradient = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Draw movie title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String title = movieTitle;
        if (fm.stringWidth(title) > width - 40) {
            while (fm.stringWidth(title + "...") > width - 40 && title.length() > 3) {
                title = title.substring(0, title.length() - 1);
            }
            title = title + "...";
        }
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (width - titleWidth) / 2, height / 2);

        g2d.dispose();
        return new ImageIcon(image);
    }

    public static void clearCache() {
        posterCache.clear();
    }
}