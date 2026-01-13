import javax.swing.*;         // GUI-componenten (JFrame, JPanel, JButton, JLabel, etc.)
import javax.swing.text.*;    // Tekststyling in JTextPane (StyledDocument, SimpleAttributeSet, StyleConstants)
import java.awt.*;            // Layouts, kleuren, fonts, cursors
import java.sql.*;            // Database-connectie, queries (Connection, DriverManager, PreparedStatement, ResultSet)

public class PovertyLiftersGUI {

    private static final int FRAME_WIDTH = 1200;  // Breedte van het hoofdframe
    private static final int FRAME_HEIGHT = 1000; // Hoogte van het hoofdframe

    public static void main(String[] args) {
        showStartPage("NL"); // Start de applicatie met Nederlandse taal
    }

    // ==================== DATABASE ====================
    public static Connection getConnection() throws SQLException {
        // Maak connectie met MySQL database
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/povertylifters", // database URL
                "root",                                       // database gebruiker
                "wachtwoord"                                  // database wachtwoord
        );
    }

    // ==================== NAVBAR ====================
    public static JPanel createNavbar(JFrame frame, String lang) {
        JPanel navbar = new JPanel(new BorderLayout()); // Panel met BorderLayout
        navbar.setBackground(Color.ORANGE);             // Achtergrondkleur
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding

        JLabel title = new JLabel("PovertyLifters");   // Titel label
        title.setFont(new Font("Arial", Font.BOLD, 22)); // Font en stijl
        title.setForeground(Color.WHITE);               // Tekstkleur
        title.setCursor(new Cursor(Cursor.HAND_CURSOR));// Cursor als hand bij hover
        title.addMouseListener(new java.awt.event.MouseAdapter() { // Klik-event
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showStartPage(lang); // Ga naar startpagina
                frame.dispose();     // Sluit huidig frame
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel voor rechts
        right.setOpaque(false); // Maak transparant

        String[] languages = {"NL - Nederlands", "EN - English", "AR - العربية", "ZH - 中文"};
        JComboBox<String> languageBox = new JComboBox<>(languages); // Dropdown voor talen
        languageBox.setFont(new Font("Arial", Font.PLAIN, 14));     // Font instellen

        // Zet geselecteerde taal in combobox
        switch (lang) {
            case "EN":
                languageBox.setSelectedIndex(1);
                break;
            case "AR":
                languageBox.setSelectedIndex(2);
                break;
            case "ZH":
                languageBox.setSelectedIndex(3);
                break;
            default:
                languageBox.setSelectedIndex(0);
        }

        languageBox.addActionListener(e -> { // Actie bij taalwissel
            String selected = (String) languageBox.getSelectedItem();
            String newLang = selected.substring(0, 2); // Haal taalcode uit geselecteerde item
            showStartPage(newLang); // Toon startpagina met nieuwe taal
            frame.dispose();        // Sluit huidig frame
        });

        JButton btnAdmin = new JButton("Admin"); // Admin knop
        btnAdmin.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAdmin.addActionListener(e -> {        // Actie bij klikken
            showAdminLoginOrRegister(lang);      // Toon admin login/register scherm
            frame.dispose();                     // Sluit huidig frame
        });

        right.add(languageBox); // Voeg combo toe
        right.add(btnAdmin);    // Voeg knop toe

        navbar.add(title, BorderLayout.WEST);   // Plaats titel links
        navbar.add(right, BorderLayout.EAST);   // Plaats knoppen rechts

        return navbar; // Return navbar panel
    }

    // ==================== START ====================
    public static void showStartPage(String lang) {
        JFrame frame = new JFrame("PovertyLifters");  // Maak nieuw frame
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);     // Stel grootte in
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Sluit app bij kruisje
        frame.setLocationRelativeTo(null);           // Zet frame in het midden
        frame.setLayout(new BorderLayout());         // BorderLayout voor hoofdframe

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH); // Voeg navbar toe

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Verticale layout

        JTextPane info = new JTextPane();            // Tekstpaneel
        info.setEditable(false);                     // Niet bewerkbaar
        info.setFont(new Font("Arial", Font.PLAIN, 22)); // Font
        info.setBackground(frame.getBackground());   // Zelfde achtergrondkleur als frame

        // Kies tekst op basis van taal
        String text;
        switch (lang) {
            case "EN":
                text = "Welcome to PovertyLifters!\n\n" +
                        "Here you can find important information for newcomers in the Netherlands:\n\n" +
                        "• Asylum Center (AZC)\n• Life in the Netherlands\n• Finances\n• Finding Work\n• Healthcare\n• Learning Language";
                break;
            case "AR":
                text = "مرحبًا بك في PovertyLifters!\n\n" +
                        "هنا يمكنك العثور على معلومات مهمة للقادمين الجدد إلى هولندا:\n\n" +
                        "• مركز اللجوء (AZC)\n• الحياة في هولندا\n• الشؤون المالية\n• البحث عن عمل\n• الرعاية الصحية\n• تعلم اللغة";
                break;
            case "ZH":
                text = "欢迎来到 PovertyLifters！\n\n" +
                        "在这里您可以找到关于荷兰新移民的重要信息：\n\n" +
                        "• 难民接收中心 (AZC)\n• 荷兰生活\n• 财务\n• 找工作\n• 医疗\n• 学习语言";
                break;
            default:
                text = "Welkom bij PovertyLifters!\n\n" +
                        "Hier vind je belangrijke informatie voor nieuwkomers in Nederland:\n\n" +
                        "• AZC\n• Het leven in Nederland\n• Geldzaken\n• Werk vinden\n• Zorg regelen\n• Taal leren";
        }

        info.setText(text);                          // Zet tekst in paneel
        StyledDocument doc = info.getStyledDocument(); // StyledDocument voor centreren
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER); // Centreer tekst
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JScrollPane scrollPane = new JScrollPane(info); // Scrollbaar paneel
        scrollPane.setBorder(null);
        centerPanel.add(scrollPane);                   // Voeg toe aan center panel

        try {
            // Voeg afbeelding toe
            ImageIcon icon = new ImageIcon("resources/Community-PNG.png");
            Image img = icon.getImage().getScaledInstance(FRAME_WIDTH - 700, 500, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centreer afbeelding
            centerPanel.add(Box.createVerticalStrut(20)); // Spacing
            centerPanel.add(imageLabel);

        } catch (Exception e) {
            System.out.println("Afbeelding niet gevonden: " + e.getMessage()); // Foutmelding
        }

        frame.add(centerPanel, BorderLayout.CENTER); // Voeg center panel toe
        frame.setVisible(true);                       // Maak frame zichtbaar
    }

    // ==================== TOPICS ====================
    public static void showTopics(String lang) {
        JFrame frame = new JFrame("PovertyLifters");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH); // Voeg navbar toe

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10)); // GridLayout voor knoppen
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        String[] topics;
        // Kies onderwerpen op basis van taal
        switch (lang) {
            case "EN":
                topics = new String[]{"Learn a language", "Find a job", "Healthcare", "Finances"};
                break;
            case "AR":
                topics = new String[]{" تعلم اللغة", " البحث عن عمل", " الرعاية الصحية", " الشؤون المالية"};
                break;
            case "ZH":
                topics = new String[]{"学习语言", "找工作", "医疗信息", "财务信息"};
                break;
            default:
                topics = new String[]{"Taal leren", "Werk vinden", "Zorg regelen", "Financiën"};
        }

        // For-loop + array concept
        for (String t : topics) {
            JButton btn = new JButton(t);           // Maak knop
            btn.setFont(new Font("Arial", Font.PLAIN, 24)); // Font
            btn.addActionListener(e -> showInfoScreen(t, lang)); // Klik actie
            panel.add(btn);                          // Voeg knop toe aan panel
        }

        frame.add(panel, BorderLayout.CENTER);       // Voeg panel toe aan frame
        frame.setVisible(true);                      // Maak frame zichtbaar
    }

    // ==================== INFO ====================
    public static void showInfoScreen(String topic, String lang) {
        JFrame frame = new JFrame("Information");    // Nieuw frame
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);                      // Niet bewerkbaar
        txt.setLineWrap(true);                       // Wrap regels
        txt.setWrapStyleWord(true);                  // Wrap per woord
        txt.setFont(new Font("Arial", Font.PLAIN, 20));

        // Kies tekst op basis van taal
        switch (lang) {
            case "EN":
                txt.setText("• Free language courses\n• Government support\n• Online platforms\n• Ask questions via help form");
                break;
            case "AR":
                txt.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // RTL
                txt.setText("• دورات لغة مجانية\n• دعم حكومي\n• منصات تعليم\n• يمكنك طرح الأسئلة");
                break;
            case "ZH":
                txt.setText("• 免费语言课程\n• 政府支持\n• 在线学习平台\n• 可提交问题");
                break;
            default:
                txt.setText("• Gratis taalcursussen\n• Gemeentehulp\n• Online platforms\n• Stel vragen via helpformulier");
        }

        frame.add(new JScrollPane(txt), BorderLayout.CENTER); // Voeg scrollpane toe

        JButton back = new JButton(
                lang.equals("EN") ? "Back" :
                        lang.equals("AR") ? "رجوع" :
                                lang.equals("ZH") ? "返回" : "Terug"
        );
        back.setFont(new Font("Arial", Font.PLAIN, 20));
        back.addActionListener(e -> {
            showTopics(lang);  // Ga terug naar topics
            frame.dispose();   // Sluit huidig frame
        });

        frame.add(back, BorderLayout.SOUTH); // Voeg terug knop toe
        frame.setVisible(true);              // Toon frame
    }

    // ==================== ADMIN ====================
    public static void showAdminLoginOrRegister(String lang) {
        JFrame frame = new JFrame("Admin");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 500, 50, 500));

        JButton login = new JButton("Login");     // Login knop
        JButton register = new JButton("Register");// Register knop

        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        register.setAlignmentX(Component.CENTER_ALIGNMENT);

        login.addActionListener(e -> {
            showAdminLogin(lang);
            frame.dispose();
        });
        register.addActionListener(e -> {
            showAdminRegistration(lang);
            frame.dispose();
        });

        panel.add(login);
        panel.add(Box.createVerticalStrut(20));
        panel.add(register);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void showAdminRegistration(String lang) {
        JFrame frame = new JFrame("Admin Registration");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 500, 50, 500));

        JLabel userLabel = new JLabel("Username:");  // Username label
        JTextField user = new JTextField(20);        // Username input

        JLabel emailLabel = new JLabel("Email:");
        JTextField email = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField pass = new JPasswordField(20);

        JButton btn = new JButton("Register");      // Register knop
        JLabel msg = new JLabel("", SwingConstants.CENTER); // Feedback label

        // Stel max size in voor BoxLayout
        user.setMaximumSize(user.getPreferredSize());
        email.setMaximumSize(email.getPreferredSize());
        pass.setMaximumSize(pass.getPreferredSize());

        user.setAlignmentX(Component.CENTER_ALIGNMENT);
        email.setAlignmentX(Component.CENTER_ALIGNMENT);
        pass.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Voeg componenten toe aan panel
        panel.add(userLabel);
        panel.add(user);
        panel.add(Box.createVerticalStrut(10));
        panel.add(emailLabel);
        panel.add(email);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passLabel);
        panel.add(pass);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msg);

        btn.addActionListener(e -> {
            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO admins (username, email, password) VALUES (?, ?, ?)"
                );
                ps.setString(1, user.getText());
                ps.setString(2, email.getText());
                ps.setString(3, new String(pass.getPassword()));
                ps.executeUpdate();         // Voer insert uit
                msg.setText("Admin registered!"); // Toon feedback
            } catch (SQLException ex) {
                msg.setText("Error: " + ex.getMessage()); // Foutmelding
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void showAdminLogin(String lang) {
        JFrame frame = new JFrame("Admin Login");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 500, 50, 500));

        JLabel userLabel = new JLabel("Username:");
        JTextField user = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField pass = new JPasswordField(20);
        JButton btn = new JButton("Login");
        JLabel msg = new JLabel("", SwingConstants.CENTER);

        user.setMaximumSize(user.getPreferredSize());
        pass.setMaximumSize(pass.getPreferredSize());

        user.setAlignmentX(Component.CENTER_ALIGNMENT);
        pass.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(userLabel);
        panel.add(user);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passLabel);
        panel.add(pass);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msg);

        btn.addActionListener(e -> {
            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement(
                        "SELECT * FROM admins WHERE username=? AND password=?");
                ps.setString(1, user.getText());
                ps.setString(2, new String(pass.getPassword()));
                ResultSet rs = ps.executeQuery();    // Voer select uit
                if (rs.next()) {                     // Check login
                    showAdminDashboard(lang);       // Toon dashboard
                    frame.dispose();
                } else {
                    msg.setText("Invalid login");   // Foutmelding
                }
            } catch (SQLException ex) {
                msg.setText("Error: " + ex.getMessage()); // Foutmelding
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // ==================== ADMIN DASHBOARD ====================
    public static void showAdminDashboard(String lang) {
        JFrame frame = new JFrame("Admin Dashboard");       // Nieuw frame voor dashboard
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH); // Voeg navbar toe

        JTextArea area = new JTextArea();                   // Tekstgebied voor helpvragen
        area.setEditable(false);                             // Niet bewerkbaar
        area.setFont(new Font("Arial", Font.PLAIN, 18));    // Font instellen

        if (!hasHelpRequests()) { // boolean + if-statement: check of er helpvragen zijn
            area.setText("No help requests yet.");          // Geen vragen
        } else {
            try (Connection c = getConnection()) {         // Database connectie
                ResultSet rs = c.prepareStatement(
                        "SELECT * FROM helpvragen ORDER BY datum DESC"
                ).executeQuery();
                while (rs.next()) { // while-loop: door alle resultaten lopen
                    area.append(
                            "Name: " + rs.getString("naam") +
                                    "\nQuestion: " + rs.getString("vraag") +
                                    "\nDate: " + rs.getTimestamp("datum") +
                                    "\n----------------------\n"
                    );
                }
            } catch (SQLException ex) {
                area.setText("Error: " + ex.getMessage()); // Foutmelding
            }
        }

        double avg = averageHelpRequestsPerDay(); // double + return-type
        area.append("\nAverage help requests per day: " + avg); // Toon gemiddelde

        frame.add(new JScrollPane(area), BorderLayout.CENTER); // Scrollable area
        frame.setVisible(true);                                 // Maak zichtbaar
    }

// ==================== EXTRA CONCEPTEN ====================

    // Controleer of er helpvragen zijn (boolean + if-statement + database)
    public static boolean hasHelpRequests() {
        try (Connection c = getConnection()) {                    // Connectie openen
            ResultSet rs = c.prepareStatement(
                    "SELECT COUNT(*) AS count FROM helpvragen"
            ).executeQuery();
            if (rs.next()) {                                      // Haal resultaat op
                int count = rs.getInt("count");                   // variabele
                boolean tooFew = count == 0;                      // boolean
                if (tooFew) {
                    return false;                                 // Geen vragen
                } else {
                    return true;                                  // Er zijn vragen
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());      // Foutmelding
        }
        return false;                                             // Default return
    }

    // Bereken gemiddelde helpvragen per dag (double + return-type)
    public static double averageHelpRequestsPerDay() {
        try (Connection c = getConnection()) {                    // Database connectie
            ResultSet rs = c.prepareStatement(
                    "SELECT COUNT(*) AS total, DATE(datum) AS day FROM helpvragen GROUP BY DATE(datum)"
            ).executeQuery();

            double sum = 0;                                      // Som van vragen
            int days = 0;                                        // Aantal dagen
            while (rs.next()) {                                  // while-loop
                sum += rs.getInt("total");                       // Tel vragen op
                days++;                                          // Verhoog dagen
            }
            if (days == 0) return 0;                             // Geen data -> 0
            return sum / days;                                   // Gemiddelde
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());     // Foutmelding
            return 0;
        }
    }

    // Toon onderwerpen in console (for-loop + array)
    public static void showTopicsArray(String lang) {
        String[] topics;
        switch (lang) {                                           // Kies taal
            case "EN":
                topics = new String[]{"Learn a language", "Find a job", "Healthcare", "Finances"};
                break;
            case "AR":
                topics = new String[]{"تعلم اللغة", "البحث عن عمل", "الرعاية الصحية", "الشؤون المالية"};
                break;
            case "ZH":
                topics = new String[]{"学习语言", "找工作", "医疗信息", "财务信息"};
                break;
            default:
                topics = new String[]{"Taal leren", "Werk vinden", "Zorg regelen", "Financiën"};
        }

        for (int i = 0; i < topics.length; i++) {               // for-loop
            System.out.println("Topic " + (i + 1) + ": " + topics[i]);
        }
    }

    // Toon helpvragen in console (while-loop + database)
    public static void listHelpQuestions() {
        try (Connection c = getConnection()) {
            ResultSet rs = c.prepareStatement(
                    "SELECT * FROM helpvragen ORDER BY datum DESC"
            ).executeQuery();
            while (rs.next()) {                                  // while-loop
                System.out.println(
                        "Name: " + rs.getString("naam") + ", Question: " + rs.getString("vraag")
                );
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());     // Foutmelding
        }
    }
}