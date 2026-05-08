package panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FoodOrderPanel extends JPanel {
    private Map<String, FoodItem> foodMenu;
    private DefaultListModel<FoodItem> cartModel;
    private JList<FoodItem> cartList;
    private JComboBox<String> foodCombo;
    private JSpinner quantitySpinner;
    private JLabel totalLabel;
    private List<FoodItem> selectedFoods;
    private JLabel itemCountLabel;

    // Modern colors
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private final Color COLOR_DANGER = new Color(231, 76, 60);
    private final Color COLOR_WARNING = new Color(241, 196, 15);
    private final Color COLOR_BORDER = new Color(220, 220, 220);
    private final Color COLOR_BG = new Color(248, 249, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_GRAY = new Color(127, 140, 141);

    public static class FoodItem {
        private String name;
        private double price;
        private String icon;

        public FoodItem(String name, double price, String icon) {
            this.name = name;
            this.price = price;
            this.icon = icon;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getIcon() { return icon; }

        @Override
        public String toString() {
            return String.format("%s %s - $%.2f", icon, name, price);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FoodItem) {
                FoodItem other = (FoodItem) obj;
                return this.name.equals(other.name) && this.price == other.price;
            }
            return false;
        }
    }

    public FoodOrderPanel() {
        this.selectedFoods = new ArrayList<>();
        initFoodMenu();
        initComponents();
    }

    private void initFoodMenu() {
        foodMenu = new LinkedHashMap<>();
        foodMenu.put("🍿 Popcorn (Large)", new FoodItem("Popcorn (Large)", 8.50, "🍿"));
        foodMenu.put("🍿 Popcorn (Medium)", new FoodItem("Popcorn (Medium)", 6.50, "🍿"));
        foodMenu.put("🥤 Soda (Large)", new FoodItem("Soda (Large)", 4.50, "🥤"));
        foodMenu.put("🥤 Soda (Medium)", new FoodItem("Soda (Medium)", 3.50, "🥤"));
        foodMenu.put("🥤 Soda (Small)", new FoodItem("Soda (Small)", 2.50, "🥤"));
        foodMenu.put("🍫 Candy Bar", new FoodItem("Candy Bar", 2.00, "🍫"));
        foodMenu.put("🍿 Nachos", new FoodItem("Nachos", 7.50, "🍿"));
        foodMenu.put("🌭 Hot Dog", new FoodItem("Hot Dog", 5.50, "🌭"));
        foodMenu.put("🍦 Ice Cream", new FoodItem("Ice Cream", 4.00, "🍦"));
        foodMenu.put("☕ Coffee", new FoodItem("Coffee", 3.50, "☕"));
        foodMenu.put("💧 Bottled Water", new FoodItem("Bottled Water", 2.00, "💧"));
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.45);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(10);

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("🍿 Food & Drinks");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Enhance your movie experience with our delicious snacks");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "Menu",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_PRIMARY
        ));

        // Menu items grid
        JPanel menuPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Map.Entry<String, FoodItem> entry : foodMenu.entrySet()) {
            FoodItem item = entry.getValue();
            JPanel itemCard = createMenuItemCard(item);
            menuPanel.add(itemCard);
        }

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(menuScroll, BorderLayout.CENTER);

        // Selection panel at bottom
        JPanel selectionPanel = createSelectionPanel();
        panel.add(selectionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMenuItemCard(FoodItem item) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel iconLabel = new JLabel(item.getIcon());
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JLabel priceLabel = new JLabel(String.format("$%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        priceLabel.setForeground(COLOR_SUCCESS);
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        JButton addBtn = new JButton("+ Add");
        addBtn.setBackground(COLOR_PRIMARY);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        addBtn.setPreferredSize(new Dimension(60, 25));
        addBtn.addActionListener(e -> quickAddToCart(item));

        card.add(iconLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(addBtn, BorderLayout.EAST);

        return card;
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Item:"), gbc);
        gbc.gridx = 1;
        foodCombo = createStyledComboBox();
        for (String item : foodMenu.keySet()) {
            foodCombo.addItem(item);
        }
        panel.add(foodCombo, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Qty:"), gbc);
        gbc.gridx = 3;
        quantitySpinner = createStyledSpinner();
        panel.add(quantitySpinner, gbc);

        gbc.gridx = 4;
        JButton addBtn = createStyledButton("Add", COLOR_SUCCESS);
        addBtn.addActionListener(e -> addToCart());
        panel.add(addBtn, gbc);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "Your Order",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_PRIMARY
        ));

        // Cart list with custom cell renderer
        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);
        cartList.setCellRenderer(new FoodItemRenderer());
        cartList.setBackground(COLOR_BG);
        cartList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(300, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statsPanel.setBackground(Color.WHITE);

        itemCountLabel = new JLabel("Items: 0");
        itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(COLOR_DANGER);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statsPanel.add(itemCountLabel);
        statsPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton removeBtn = createStyledButton("Remove Selected", COLOR_DANGER);
        removeBtn.addActionListener(e -> removeFromCart());

        JButton clearBtn = createStyledButton("Clear All", COLOR_WARNING);
        clearBtn.addActionListener(e -> clearOrder());

        buttonPanel.add(removeBtn);
        buttonPanel.add(clearBtn);

        summaryPanel.add(statsPanel, BorderLayout.CENTER);
        summaryPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void quickAddToCart(FoodItem item) {
        selectedFoods.add(item);
        cartModel.addElement(item);
        updateTotal();

        // Visual feedback
        JOptionPane.showMessageDialog(this,
                "✓ " + item.getName() + " added to your order!",
                "Added",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addToCart() {
        String foodName = (String) foodCombo.getSelectedItem();
        FoodItem selectedFood = foodMenu.get(foodName);
        int quantity = (Integer) quantitySpinner.getValue();

        for (int i = 0; i < quantity; i++) {
            selectedFoods.add(selectedFood);
            cartModel.addElement(selectedFood);
        }
        updateTotal();

        JOptionPane.showMessageDialog(this,
                quantity + " x " + selectedFood.getName() + " added!\nTotal: " + totalLabel.getText(),
                "Added to Order",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeFromCart() {
        int selectedIndex = cartList.getSelectedIndex();
        if (selectedIndex >= 0) {
            FoodItem removed = cartModel.getElementAt(selectedIndex);
            cartModel.remove(selectedIndex);

            for (int i = 0; i < selectedFoods.size(); i++) {
                if (selectedFoods.get(i).equals(removed)) {
                    selectedFoods.remove(i);
                    break;
                }
            }
            updateTotal();

            JOptionPane.showMessageDialog(this,
                    "✗ " + removed.getName() + " removed from your order!",
                    "Removed",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an item to remove!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTotal() {
        double total = 0;
        for (FoodItem food : selectedFoods) {
            total += food.getPrice();
        }
        totalLabel.setText(String.format("$%.2f", total));
        itemCountLabel.setText(String.format("Items: %d", selectedFoods.size()));
    }

    public List<FoodItem> getSelectedFoods() {
        return selectedFoods;
    }

    public void clearOrder() {
        selectedFoods.clear();
        cartModel.clear();
        updateTotal();
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return combo;
    }

    private JSpinner createStyledSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        editor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return spinner;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Custom list cell renderer for food items
    class FoodItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof FoodItem) {
                FoodItem item = (FoodItem) value;
                String displayText = String.format("%s %s - $%.2f",
                        item.getIcon(), item.getName(), item.getPrice());
                super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);

                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                setFont(new Font("Segoe UI", Font.PLAIN, 12));

                if (!isSelected) {
                    setBackground(index % 2 == 0 ? Color.WHITE : COLOR_BG);
                }
            } else {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            return this;
        }
    }
}