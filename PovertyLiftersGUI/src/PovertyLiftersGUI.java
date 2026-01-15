import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.sql.*; // later gebruiken voor MySQL

public class PovertyLiftersGUI {

    // ==================== CONSTANTEN (APP SETTINGS) ====================
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 1000;

    // Session: alleen zolang app draait
    private static String currentUser = null;

    // Taal codes
    private static final String LANG_NL = "NL";
    private static final String LANG_EN = "EN";
    private static final String LANG_AR = "AR";
    private static final String LANG_ZH = "ZH";

    // ==================== TOPIC DATA (ARRAYS) ====================
    // Vaste keys om teksten te vinden
    private static final String[] topicKeys = {"AZC", "LANGUAGE", "WORK", "HEALTHCARE", "FINANCE"};

    // Labels per taal (zelfde volgorde als topicKeys)
    private static final String[] topicsNL = {"AZC", "Taal leren", "Werk vinden", "Zorg regelen", "Financiën"};
    private static final String[] topicsEN = {"Asylum Center (AZC)", "Learn a language", "Find a job", "Healthcare", "Finances"};
    private static final String[] topicsAR = {"مركز اللجوء (AZC)", "تعلم اللغة", "البحث عن عمل", "الرعاية الصحية", "الشؤون المالية"};
    private static final String[] topicsZH = {"难民接收中心 (AZC)", "学习语言", "找工作", "医疗信息", "财务信息"};

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showStartPage(LANG_NL));
    }

    // ==================== (LATER) DATABASE CONNECTIE ====================
    // TODO: later DB gegevens hier invullen
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/povertylifters",
                "root",
                "wachtwoord"
        );
    }

    // ==================== TRANSLATION HELPER ====================
    private static String t(String lang, String nl, String en, String ar, String zh) {
        switch (lang) {
            case LANG_EN: return en;
            case LANG_AR: return ar;
            case LANG_ZH: return zh;
            default: return nl;
        }
    }

    // ==================== BASIC UI HELPERS ====================

    private static JFrame createBaseFrame(String title, String lang) {
        JFrame frame = new JFrame(title);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(createNavbar(frame, lang), BorderLayout.NORTH);
        return frame;
    }

    private static void openPage(JFrame oldFrame, Runnable openNewPage) {
        openNewPage.run();
        if (oldFrame != null) oldFrame.dispose();
    }

    private static JButton createButton(String text, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        return btn;
    }

    private static void centerTextPane(JTextPane pane) {
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    private static JLabel centerLabel(String text, int fontSize) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, fontSize));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private static void setFieldSize(JComponent field, int width, int height) {
        Dimension d = new Dimension(width, height);
        field.setPreferredSize(d);
        field.setMaximumSize(d);
        field.setMinimumSize(d);
    }

    private static JPanel createCenteredFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        wrapper.setOpaque(false);

        wrapper.add(form);
        return wrapper;
    }

    // ==================== NAVBAR ====================
    public static JPanel createNavbar(JFrame frame, String lang) {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Color.ORANGE);
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title (klikbaar)
        JLabel title = new JLabel("PovertyLifters");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setCursor(new Cursor(Cursor.HAND_CURSOR));
        title.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openPage(frame, () -> showStartPage(lang));
            }
        });

        // Rechterkant: taal + user + admin
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        // Language dropdown
        String[] languages = {"NL - Nederlands", "EN - English", "AR - العربية", "ZH - 中文"};
        JComboBox<String> languageBox = new JComboBox<>(languages);
        languageBox.setFont(new Font("Arial", Font.PLAIN, 14));

        if (LANG_EN.equals(lang)) {
            languageBox.setSelectedIndex(1);
        } else if (LANG_AR.equals(lang)) {
            languageBox.setSelectedIndex(2);
        } else if (LANG_ZH.equals(lang)) {
            languageBox.setSelectedIndex(3);
        } else {
            languageBox.setSelectedIndex(0);
        }

        languageBox.addActionListener(e -> {
            String selected = (String) languageBox.getSelectedItem();
            String newLang = selected.substring(0, 2);
            openPage(frame, () -> showStartPage(newLang));
        });

        // User panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userPanel.setOpaque(false);

        if (currentUser == null) {
            JButton login = createButton(t(lang, "Login", "Login", "تسجيل الدخول", "登录"), 14);
            login.addActionListener(e -> openPage(frame, () -> showUserLogin(lang)));

            JButton register = createButton(t(lang, "Account maken", "Register", "إنشاء حساب", "注册"), 14);
            register.addActionListener(e -> openPage(frame, () -> showUserRegister(lang)));

            userPanel.add(login);
            userPanel.add(register);
        } else {
            JLabel welcome = new JLabel(t(lang, "Welkom, ", "Welcome, ", "مرحباً، ", "欢迎，") + currentUser);
            welcome.setForeground(Color.WHITE);
            welcome.setFont(new Font("Arial", Font.BOLD, 14));

            JButton logout = createButton(t(lang, "Logout", "Logout", "تسجيل الخروج", "退出"), 14);
            logout.addActionListener(e -> {
                currentUser = null;
                openPage(frame, () -> showStartPage(lang));
            });

            userPanel.add(welcome);
            userPanel.add(logout);
        }

        JButton admin = createButton("Admin", 14);
        admin.addActionListener(e -> openPage(frame, () -> showAdminLoginOrRegister(lang)));

        right.add(languageBox);
        right.add(userPanel);
        right.add(admin);

        navbar.add(title, BorderLayout.WEST);
        navbar.add(right, BorderLayout.EAST);

        return navbar;
    }

    // ==================== START PAGE ====================
    public static void showStartPage(String lang) {
        JFrame frame = createBaseFrame("PovertyLifters", lang);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JTextPane info = new JTextPane();
        info.setEditable(false);
        info.setFont(new Font("Arial", Font.PLAIN, 22));
        info.setBackground(frame.getBackground());

        String text;
        switch (lang) {
            case LANG_EN:
                text = "Welcome to PovertyLifters!\n\n" +
                        "PovertyLifters is a platform designed to support newcomers in the Netherlands.\n\n" +
                        "Here you can find clear, reliable, and easy-to-understand information about daily life.\n\n" +
                        "The platform helps you with topics such as asylum centers (AZC), learning the Dutch language, finding work, accessing healthcare, and managing your finances.";
                break;

            case LANG_AR:
                text = "مرحبًا بك في PovertyLifters!\n\n" +
                        "PovertyLifters هي منصة تم إنشاؤها لدعم القادمين الجدد إلى هولندا.\n\n" +
                        "ستجد هنا معلومات واضحة وموثوقة وسهلة الفهم حول الحياة اليومية.\n\n" +
                        "تساعدك المنصة في مواضيع مثل مراكز اللجوء (AZC)، وتعلم اللغة الهولندية، والعثور على عمل، والحصول على الرعاية الصحية، وإدارة الشؤون المالية.";
                break;

            case LANG_ZH:
                text = "欢迎来到 PovertyLifters！\n\n" +
                        "PovertyLifters 是一个专为荷兰新移民设计的支持平台。\n\n" +
                        "在这里，您可以找到关于日常生活的清晰、可靠且易于理解的信息。\n\n" +
                        "该平台帮助您了解难民接收中心（AZC）、学习荷兰语、寻找工作、获得医疗服务以及管理个人财务。";
                break;

            default:
                text = "Welkom bij PovertyLifters!\n\n" +
                        "PovertyLifters is een platform dat speciaal is ontwikkeld om nieuwkomers in Nederland te ondersteunen.\n\n" +
                        "Je vindt hier duidelijke, betrouwbare en makkelijk te begrijpen informatie over het dagelijks leven.\n\n" +
                        "Het platform helpt je met onderwerpen zoals AZC’s, het leren van de Nederlandse taal, werk vinden, toegang tot zorg en het regelen van je financiën.";
        }

        info.setText(text);
        centerTextPane(info);

        JScrollPane scrollPane = new JScrollPane(info);
        scrollPane.setBorder(null);
        centerPanel.add(scrollPane);

        // Optionele afbeelding
        try {
            ImageIcon icon = new ImageIcon("resources/Community-PNG.png");
            Image img = icon.getImage().getScaledInstance(FRAME_WIDTH - 700, 500, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            centerPanel.add(Box.createVerticalStrut(20));
            centerPanel.add(imageLabel);
        } catch (Exception e) {
            System.out.println("Afbeelding niet gevonden: " + e.getMessage());
        }

        JButton topicsBtn = createButton(t(lang, "Open onderwerpen", "Open topics", "المواضيع", "打开主题"), 22);
        topicsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        topicsBtn.addActionListener(e -> openPage(frame, () -> showTopics(lang)));

        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(topicsBtn);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // ==================== TOPICS ====================
    public static void showTopics(String lang) {
        JFrame frame = createBaseFrame("Topics", lang);

        String[] topics = getTopicsForLanguage(lang);

        JPanel panel = new JPanel(new GridLayout(topics.length, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        for (int i = 0; i < topics.length; i++) {
            String topicLabel = topics[i];
            JButton btn = createButton(topicLabel, 24);

            btn.addActionListener(e -> openPage(frame, () -> showInfoScreen(topicLabel, lang)));
            panel.add(btn);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static String[] getTopicsForLanguage(String lang) {
        if (LANG_EN.equals(lang)) return topicsEN;
        if (LANG_AR.equals(lang)) return topicsAR;
        if (LANG_ZH.equals(lang)) return topicsZH;
        return topicsNL;
    }

    // ==================== INFO SCREEN ====================
    public static void showInfoScreen(String topicLabel, String lang) {
        JFrame frame = createBaseFrame("Information", lang);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setFont(new Font("Arial", Font.PLAIN, 20));
        txt.setMargin(new Insets(18, 18, 18, 18));

        // RTL voor Arabisch
        if (LANG_AR.equals(lang)) {
            txt.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        int topicIndex = findTopicIndex(lang, topicLabel);
        String topicKey = getTopicKey(topicIndex);

        txt.setText(getInfoTextByKey(lang, topicKey));

        frame.add(new JScrollPane(txt), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));

        JButton back = createButton(t(lang, "Terug", "Back", "رجوع", "返回"), 20);
        back.addActionListener(e -> openPage(frame, () -> showTopics(lang)));

        JButton help = createButton(t(lang, "Stel een vraag", "Ask a question", "اطرح سؤالاً", "提交问题"), 20);
        help.addActionListener(e -> openPage(frame, () -> showHelpForm(lang, topicLabel)));

        bottom.add(back);
        bottom.add(help);

        frame.add(bottom, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static int findTopicIndex(String lang, String topicLabel) {
        String[] topics = getTopicsForLanguage(lang);

        for (int i = 0; i < topics.length; i++) {
            if (topics[i].equals(topicLabel)) {
                return i;
            }
        }
        return -1;
    }

    private static String getTopicKey(int topicIndex) {
        boolean isValid = topicIndex >= 0 && topicIndex < topicKeys.length;
        if (isValid) {
            return topicKeys[topicIndex];
        }
        return "UNKNOWN";
    }

    private static String getInfoTextByKey(String lang, String key) {
        if ("AZC".equals(key)) return getAzcText(lang);
        if ("LANGUAGE".equals(key)) return getLanguageText(lang);
        if ("WORK".equals(key)) return getWorkText(lang);
        if ("HEALTHCARE".equals(key)) return getHealthcareText(lang);
        if ("FINANCE".equals(key)) return getFinanceText(lang);

        return t(lang,
                "Algemene info:\n\nHier komt later meer informatie.",
                "General info:\n\nMore information will be added here later.",
                "معلومات عامة:\n\nستتم إضافة المزيد من المعلومات لاحقًا هنا.",
                "一般信息：\n\n稍后会在此添加更多信息。"
        );
    }

    // ==================== ORIGINELE TEKSTEN (ONVERANDERD) ====================

    private static String getAzcText(String lang) {
        return t(lang,
                // NL (origineel)
                "AZC (Asielzoekerscentrum) – uitgebreide info\n\n" +
                        "In een AZC woon je tijdelijk terwijl je asielprocedure loopt. In het AZC zijn er afspraken, regels en ondersteuning.\n\n" +
                        "1) Registratie & afspraken\n" +
                        "• Na aankomst krijg je vaak uitleg over jouw locatie, regels en belangrijke contactpersonen.\n" +
                        "• Houd brieven/afspraken goed bij. Als je een afspraak mist, kan dit gevolgen hebben.\n\n" +
                        "2) Huisregels (belangrijk)\n" +
                        "• Rusttijden: houd rekening met buren en gezamenlijke ruimtes.\n" +
                        "• Roken/alcohol: volg de regels die in jouw AZC gelden.\n" +
                        "• Veiligheid: meld problemen (ruzies, diefstal, intimidatie) zo snel mogelijk.\n\n" +
                        "3) Hulp & ondersteuning\n" +
                        "• COA (Centraal Orgaan opvang asielzoekers): helpt bij wonen/regels/voorzieningen.\n" +
                        "• VluchtelingenWerk (soms aanwezig): kan uitleg geven over procedure en praktische hulp.\n" +
                        "• Medische post (locatie verschilt): voor zorg en doorverwijzing.\n\n" +
                        "4) Handige tips\n" +
                        "• Bewaar documenten (ID, brieven, pasjes) op één veilige plek.\n" +
                        "• Vraag altijd om een tolk als je de taal niet goed begrijpt.\n" +
                        "• Gebruik het helpformulier in de app als je iets niet snapt.\n\n" +
                        "Veelvoorkomende vragen:\n" +
                        "• Hoe regel ik een afspraak?\n" +
                        "• Waar kan ik medische hulp krijgen?\n" +
                        "• Hoe werkt het met post en brieven?\n",
                // EN (origineel)
                "Asylum Center (AZC) – detailed information\n\n" +
                        "An AZC is a temporary place to live while your asylum procedure is ongoing. There are appointments, rules, and support services.\n\n" +
                        "1) Registration & appointments\n" +
                        "• After arrival you usually receive information about your location, rules, and key contacts.\n" +
                        "• Keep letters and appointments safe. Missing appointments can have consequences.\n\n" +
                        "2) House rules\n" +
                        "• Quiet hours: respect neighbors and shared areas.\n" +
                        "• Smoking/alcohol: follow the rules in your AZC.\n" +
                        "• Safety: report conflicts, theft, or harassment quickly.\n\n" +
                        "3) Support\n" +
                        "• COA: helps with housing, rules, and facilities.\n" +
                        "• Refugee support organizations (sometimes on-site): practical guidance.\n" +
                        "• Medical services (varies by location): care and referrals.\n\n" +
                        "4) Useful tips\n" +
                        "• Store documents (ID, letters, cards) in one safe place.\n" +
                        "• Ask for an interpreter if you do not understand the language.\n" +
                        "• Use the help form in the app if something is unclear.\n",
                // AR (origineel)
                "مركز اللجوء (AZC) – معلومات مفصلة\n\n" +
                        "يُعدّ مركز AZC مكانًا للإقامة المؤقتة أثناء سير إجراءات اللجوء. توجد مواعيد وقواعد وخدمات دعم.\n\n" +
                        "1) التسجيل والمواعيد\n" +
                        "• بعد الوصول تحصل عادةً على معلومات عن المكان والقواعد وجهات الاتصال المهمة.\n" +
                        "• احتفظ بالرسائل والمواعيد جيدًا، فقد يؤثر تفويت الموعد على ملفك.\n\n" +
                        "2) القواعد\n" +
                        "• أوقات الهدوء: احترم الجيران والأماكن المشتركة.\n" +
                        "• التدخين/الكحول: اتبع قواعد المركز.\n" +
                        "• السلامة: بلّغ بسرعة عن أي مشكلة أو تهديد.\n\n" +
                        "3) الدعم\n" +
                        "• COA: يساعد في السكن والقواعد والخدمات.\n" +
                        "• منظمات دعم (قد تكون موجودة): مساعدات عملية وإرشاد.\n" +
                        "• خدمات طبية: للرعاية والإحالة.\n\n" +
                        "4) نصائح\n" +
                        "• ضع الوثائق المهمة في مكان آمن.\n" +
                        "• اطلب مترجمًا إذا لم تفهم اللغة.\n" +
                        "• استخدم نموذج المساعدة في التطبيق عند الحاجة.\n",
                // ZH (origineel)
                "难民接收中心（AZC）— 详细信息\n\n" +
                        "AZC 是在庇护程序进行期间的临时居住地点。这里会有预约、规定和支持服务。\n\n" +
                        "1) 登记与预约\n" +
                        "• 抵达后通常会收到关于住所、规则和联系人信息。\n" +
                        "• 请妥善保存信件和预约信息，错过预约可能会带来影响。\n\n" +
                        "2) 住宿规定\n" +
                        "• 安静时间：尊重邻居与公共区域。\n" +
                        "• 吸烟/酒精：遵守中心规定。\n" +
                        "• 安全：发生冲突、盗窃或骚扰请尽快报告。\n\n" +
                        "3) 支持服务\n" +
                        "• COA：协助住宿、规则与设施。\n" +
                        "• 支援组织（部分地点有）：提供实际帮助。\n" +
                        "• 医疗服务：就医与转诊。\n\n" +
                        "4) 实用建议\n" +
                        "• 重要文件集中保管。\n" +
                        "• 听不懂时可申请翻译。\n" +
                        "• 不清楚的问题可通过求助表单咨询。\n"
        );
    }

    private static String getLanguageText(String lang) {
        return t(lang,
                // NL (origineel)
                "Taal leren – uitgebreide info\n\n" +
                        "Nederlands leren helpt bij werk, school, zorg en contact met anderen. Je kunt op verschillende manieren beginnen.\n\n" +
                        "1) Praktische start\n" +
                        "• Begin met simpele zinnen: jezelf voorstellen, boodschappen, afspraken.\n" +
                        "• Oefen elke dag 10–20 minuten (beter kort dan één keer lang).\n\n" +
                        "2) Leren via cursussen\n" +
                        "• Gemeente/taalscholen: soms gratis of met ondersteuning.\n" +
                        "• Taalmaatje/taalcafé: oefenen met vrijwilligers.\n\n" +
                        "3) Online oefenen\n" +
                        "• Gebruik apps/video’s en luister naar langzaam Nederlands.\n" +
                        "• Schrijf 3 zinnen per dag en laat iemand verbeteren.\n\n" +
                        "4) Handige tips\n" +
                        "• Vraag mensen om langzaam te praten.\n" +
                        "• Durf fouten te maken: dat is normaal.\n" +
                        "• Noteer nieuwe woorden + betekenis in jouw taal.\n",
                // EN (origineel)
                "Learning a language – detailed info\n\n" +
                        "Learning Dutch helps with work, school, healthcare, and daily life. You can start in small steps.\n\n" +
                        "1) Quick start\n" +
                        "• Learn basic sentences: introductions, shopping, appointments.\n" +
                        "• Practice daily 10–20 minutes.\n\n" +
                        "2) Courses\n" +
                        "• Municipality/language schools: sometimes free or supported.\n" +
                        "• Language cafés / volunteers: practice speaking.\n\n" +
                        "3) Online practice\n" +
                        "• Apps/videos and listening exercises.\n" +
                        "• Write a few sentences each day and get feedback.\n\n" +
                        "4) Tips\n" +
                        "• Ask people to speak slowly.\n" +
                        "• Making mistakes is normal.\n" +
                        "• Keep a small vocabulary notebook.\n",
                // AR (origineel)
                "تعلم اللغة – معلومات مفصلة\n\n" +
                        "تعلم الهولندية يساعدك في العمل والدراسة والرعاية الصحية والحياة اليومية.\n\n" +
                        "1) بداية بسيطة\n" +
                        "• تعلم جمل أساسية للتعريف بالنفس والتسوق والمواعيد.\n" +
                        "• تمرّن يوميًا 10–20 دقيقة.\n\n" +
                        "2) دورات\n" +
                        "• البلدية/مدارس اللغة: أحيانًا تكون مجانية أو مدعومة.\n" +
                        "• مقاهي اللغة/متطوعون: للتدريب على المحادثة.\n\n" +
                        "3) تدريب عبر الإنترنت\n" +
                        "• تطبيقات وفيديوهات وتمارين استماع.\n" +
                        "• اكتب جُملاً يوميًا واطلب تصحيحها.\n\n" +
                        "4) نصائح\n" +
                        "• اطلب من الناس التحدث ببطء.\n" +
                        "• الأخطاء طبيعية.\n" +
                        "• دوّن الكلمات الجديدة.\n",
                // ZH (origineel)
                "学习语言 – 详细信息\n\n" +
                        "学习荷兰语有助于找工作、上学、就医以及日常沟通。\n\n" +
                        "1) 快速入门\n" +
                        "• 学习基础句子：自我介绍、购物、预约。\n" +
                        "• 每天练习 10–20 分钟。\n\n" +
                        "2) 课程学习\n" +
                        "• 市政府/语言学校：有时免费或有补助。\n" +
                        "• 语言咖啡/志愿者：练习口语。\n\n" +
                        "3) 在线练习\n" +
                        "• 应用/视频/听力练习。\n" +
                        "• 每天写几句并请人帮你改。\n\n" +
                        "4) 建议\n" +
                        "• 请别人说慢一点。\n" +
                        "• 犯错很正常。\n" +
                        "• 记单词本很有用。\n"
        );
    }

    private static String getWorkText(String lang) {
        return t(lang,
                // NL (origineel)
                "Werk vinden – uitgebreide info\n\n" +
                        "Werk zoeken kan lastig zijn, vooral als je de taal nog leert. Begin stap voor stap.\n\n" +
                        "1) Voorbereiding\n" +
                        "• Maak een simpel CV met: naam, contact, ervaring, opleiding, vaardigheden.\n" +
                        "• Denk na over wat je kunt: schoonmaak, horeca, logistiek, techniek, zorg, etc.\n\n" +
                        "2) Waar zoeken?\n" +
                        "• Uitzendbureaus en vacaturesites.\n" +
                        "• Gemeente/werkcoach (soms via AZC/partners).\n" +
                        "• Netwerk: vraag mensen in je omgeving.\n\n" +
                        "3) Sollicitatietips\n" +
                        "• Schrijf korte motivatie: wie je bent + wat je wilt leren/doen.\n" +
                        "• Oefen een gesprek met iemand.\n\n" +
                        "4) Let op\n" +
                        "• Pas op voor nep-vacatures. Betaal nooit geld om te mogen werken.\n",
                // EN (origineel)
                "Finding a job – detailed info\n\n" +
                        "Job searching can be difficult, especially while learning the language. Start step by step.\n\n" +
                        "1) Preparation\n" +
                        "• Create a simple CV: contact info, experience, education, skills.\n" +
                        "• List what you can do (cleaning, hospitality, logistics, etc.).\n\n" +
                        "2) Where to search\n" +
                        "• Agencies and job platforms.\n" +
                        "• Municipality support / job coach.\n" +
                        "• Your network: ask people around you.\n\n" +
                        "3) Tips\n" +
                        "• Write a short motivation.\n" +
                        "• Practice interviews.\n\n" +
                        "4) Warning\n" +
                        "• Avoid scams. Never pay money to “get” a job.\n",
                // AR (origineel)
                "البحث عن عمل – معلومات مفصلة\n\n" +
                        "قد يكون البحث عن عمل صعبًا خصوصًا أثناء تعلم اللغة، لذلك ابدأ خطوة بخطوة.\n\n" +
                        "1) التحضير\n" +
                        "• جهّز سيرة ذاتية بسيطة.\n" +
                        "• اكتب مهاراتك وخبراتك.\n\n" +
                        "2) أين تبحث؟\n" +
                        "• وكالات التوظيف ومواقع الوظائف.\n" +
                        "• دعم البلدية/مرشد وظيفي.\n" +
                        "• اسأل من حولك.\n\n" +
                        "3) نصائح\n" +
                        "• اكتب رسالة قصيرة عن نفسك.\n" +
                        "• تدرب على مقابلة العمل.\n\n" +
                        "4) انتبه\n" +
                        "• تجنب الاحتيال ولا تدفع مالًا للحصول على وظيفة.\n",
                // ZH (origineel)
                "找工作 – 详细信息\n\n" +
                        "找工作可能不容易，尤其在学习语言阶段。可以循序渐进。\n\n" +
                        "1) 准备\n" +
                        "• 做一份简单简历：联系方式、经历、教育、技能。\n" +
                        "• 列出你会做的工作类型。\n\n" +
                        "2) 去哪里找\n" +
                        "• 中介/招聘网站。\n" +
                        "• 市政府支持/就业辅导。\n" +
                        "• 通过熟人介绍。\n\n" +
                        "3) 建议\n" +
                        "• 写简短动机说明。\n" +
                        "• 练习面试。\n\n" +
                        "4) 提醒\n" +
                        "• 注意诈骗：不要为了“得到工作”而付钱。\n"
        );
    }

    private static String getHealthcareText(String lang) {
        return t(lang,
                // NL (origineel)
                "Zorg regelen – uitgebreide info\n\n" +
                        "In Nederland kun je medische hulp krijgen via de huisarts. Bij spoed bel je een noodnummer.\n\n" +
                        "1) Huisarts (eerste stap)\n" +
                        "• De huisarts is meestal het startpunt voor klachten.\n" +
                        "• De huisarts kan je doorverwijzen naar een specialist.\n\n" +
                        "2) Spoed\n" +
                        "• Bij levensgevaar: bel 112.\n" +
                        "• Bij dringende klachten buiten werktijden: huisartsenpost.\n\n" +
                        "3) Medicatie\n" +
                        "• Neem lijstjes mee van medicijnen die je gebruikt.\n\n" +
                        "4) Tips\n" +
                        "• Vraag om een tolk als het nodig is.\n" +
                        "• Schrijf klachten van tevoren op.\n",
                // EN (origineel)
                "Healthcare – detailed info\n\n" +
                        "In the Netherlands, the GP (huisarts) is usually the first point of contact. In emergencies, call the emergency number.\n\n" +
                        "1) GP (first step)\n" +
                        "• The GP handles most health issues.\n" +
                        "• They can refer you to specialists.\n\n" +
                        "2) Emergency\n" +
                        "• Life-threatening: call 112.\n" +
                        "• Outside office hours: GP emergency service (huisartsenpost).\n\n" +
                        "3) Medication\n" +
                        "• Bring a list of medicines you use.\n\n" +
                        "4) Tips\n" +
                        "• Ask for an interpreter if needed.\n" +
                        "• Write your symptoms down beforehand.\n",
                // AR (origineel)
                "الرعاية الصحية – معلومات مفصلة\n\n" +
                        "في هولندا غالبًا يكون طبيب الأسرة (huisarts) هو الخطوة الأولى. في الحالات الطارئة اتصل برقم الطوارئ.\n\n" +
                        "1) طبيب الأسرة\n" +
                        "• هو نقطة البداية لمعظم المشاكل الصحية.\n" +
                        "• يمكنه إحالتك إلى اختصاصي.\n\n" +
                        "2) الطوارئ\n" +
                        "• خطر على الحياة: اتصل بـ 112.\n" +
                        "• خارج ساعات العمل: خدمة طبيب المناوبة.\n\n" +
                        "3) الأدوية\n" +
                        "• أحضر قائمة بالأدوية التي تستخدمها.\n\n" +
                        "4) نصائح\n" +
                        "• اطلب مترجمًا عند الحاجة.\n" +
                        "• دوّن الأعراض مسبقًا.\n",
                // ZH (origineel)
                "医疗信息 – 详细信息\n\n" +
                        "在荷兰通常先联系家庭医生（huisarts）。紧急情况请拨打急救电话。\n\n" +
                        "1) 家庭医生\n" +
                        "• 大多数健康问题从家庭医生开始。\n" +
                        "• 可转诊专科。\n\n" +
                        "2) 急诊\n" +
                        "• 危及生命：拨打 112。\n" +
                        "• 非工作时间：家庭医生急诊服务（huisartsenpost）。\n\n" +
                        "3) 药物\n" +
                        "• 带上你正在使用药物的清单。\n\n" +
                        "4) 建议\n" +
                        "• 需要时可申请翻译。\n" +
                        "• 提前写下症状。\n"
        );
    }

    private static String getFinanceText(String lang) {
        return t(lang,
                // NL (origineel)
                "Financiën – uitgebreide info\n\n" +
                        "Geldzaken kunnen verwarrend zijn in een nieuw land. Met kleine stappen krijg je meer controle.\n\n" +
                        "1) Basis\n" +
                        "• Maak een overzicht: inkomsten (toeslagen/werk) en uitgaven (huur, eten, vervoer).\n" +
                        "• Zet vaste kosten eerst op een rij.\n\n" +
                        "2) Budget tips\n" +
                        "• Boodschappenlijstje maken.\n" +
                        "• Spaar (als het lukt) een klein bedrag per week.\n\n" +
                        "3) Veiligheid\n" +
                        "• Deel nooit pincodes of wachtwoorden.\n" +
                        "• Let op nep-berichten die om geld vragen.\n\n" +
                        "4) Hulp\n" +
                        "• Vraag hulp aan een budgetcoach/maatschappelijk werk (gemeente/organisatie).\n",
                // EN (origineel)
                "Finances – detailed info\n\n" +
                        "Money matters can be confusing in a new country. Small steps help you stay in control.\n\n" +
                        "1) Basics\n" +
                        "• Make an overview: income and expenses.\n" +
                        "• List fixed costs first.\n\n" +
                        "2) Budget tips\n" +
                        "• Use a shopping list.\n" +
                        "• Save a small amount weekly if possible.\n\n" +
                        "3) Safety\n" +
                        "• Never share PIN codes or passwords.\n" +
                        "• Watch out for scam messages asking for money.\n\n" +
                        "4) Help\n" +
                        "• Ask a budget coach or social worker (municipality/organization).\n",
                // AR (origineel)
                "الشؤون المالية – معلومات مفصلة\n\n" +
                        "قد تكون الأمور المالية مربكة في بلد جديد. خطوات صغيرة تساعدك على التنظيم.\n\n" +
                        "1) الأساسيات\n" +
                        "• راقب الدخل والمصاريف.\n" +
                        "• ابدأ بالمصاريف الثابتة.\n\n" +
                        "2) نصائح\n" +
                        "• اكتب قائمة مشتريات.\n" +
                        "• حاول ادخار مبلغ صغير أسبوعيًا.\n\n" +
                        "3) الأمان\n" +
                        "• لا تشارك الرقم السري أو كلمات المرور.\n" +
                        "• انتبه لرسائل الاحتيال.\n\n" +
                        "4) المساعدة\n" +
                        "• اطلب مساعدة من مرشد مالي/عامل اجتماعي.\n",
                // ZH (origineel)
                "财务信息 – 详细信息\n\n" +
                        "在新国家处理财务可能会比较困难。循序渐进会更容易掌控。\n\n" +
                        "1) 基础\n" +
                        "• 记录收入与支出。\n" +
                        "• 先列出固定开销。\n\n" +
                        "2) 预算建议\n" +
                        "• 做购物清单。\n" +
                        "• 能的话每周存一点。\n\n" +
                        "3) 安全\n" +
                        "• 不要分享银行卡密码或账户密码。\n" +
                        "• 注意诈骗信息。\n\n" +
                        "4) 求助\n" +
                        "• 可向预算辅导/社工寻求帮助。\n"
        );
    }

    // ==================== HELP FORM ====================
    public static void showHelpForm(String lang, String topicLabel) {
        JFrame frame = createBaseFrame("Help Form", lang);

        JPanel wrapper = createCenteredFormPanel();
        JPanel form = (JPanel) wrapper.getComponent(0);

        JLabel title = new JLabel(t(lang,
                "Helpformulier (" + topicLabel + ")",
                "Help form (" + topicLabel + ")",
                "نموذج المساعدة (" + topicLabel + ")",
                "求助表单 (" + topicLabel + ")"
        ));
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = centerLabel(t(lang, "Naam", "Name", "الاسم", "姓名"), 16);
        JTextField nameField = new JTextField();
        setFieldSize(nameField, 420, 40);

        if (currentUser != null) {
            nameField.setText(currentUser);
            nameField.setEditable(false);
        }

        JLabel questionLabel = centerLabel(t(lang, "Vraag", "Question", "السؤال", "问题"), 16);
        JTextArea questionArea = new JTextArea(6, 30);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        JScrollPane qScroll = new JScrollPane(questionArea);
        qScroll.setMaximumSize(new Dimension(420, 200));
        qScroll.setPreferredSize(new Dimension(420, 200));

        JButton sendBtn = createButton(t(lang, "Verzenden", "Send", "إرسال", "发送"), 18);
        sendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFieldSize(sendBtn, 220, 45);

        JLabel msg = new JLabel("");
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setFont(new Font("Arial", Font.PLAIN, 14));

        form.add(title);
        form.add(Box.createVerticalStrut(25));
        form.add(nameLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(nameField);
        form.add(Box.createVerticalStrut(14));
        form.add(questionLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(qScroll);
        form.add(Box.createVerticalStrut(22));
        form.add(sendBtn);
        form.add(Box.createVerticalStrut(12));
        form.add(msg);

        sendBtn.addActionListener(e -> {
            String naam = nameField.getText().trim();
            String vraag = questionArea.getText().trim();

            if (naam.isEmpty() || vraag.isEmpty()) {
                msg.setText(t(lang, "Vul alles in.", "Fill in everything.", "يرجى تعبئة جميع الحقول.", "请填写所有内容。"));
                return;
            }

            // TODO DB: INSERT INTO helpvragen (naam, vraag, datum, topic) VALUES (?,?,NOW(),?)
            msg.setText(t(lang, "Verzonden (demo).", "Sent (demo).", "تم الإرسال (تجريبي).", "已发送（演示）。"));
            questionArea.setText("");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        JButton back = createButton(t(lang, "Terug", "Back", "رجوع", "返回"), 20);
        back.addActionListener(e -> openPage(frame, () -> showTopics(lang)));

        bottom.add(back);

        frame.add(wrapper, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // ==================== USER LOGIN ====================
    public static void showUserLogin(String lang) {
        JFrame frame = createBaseFrame(t(lang, "Login", "Login", "تسجيل الدخول", "登录"), lang);

        JPanel wrapper = createCenteredFormPanel();
        JPanel form = (JPanel) wrapper.getComponent(0);

        JLabel title = new JLabel(t(lang, "Inloggen", "Login", "تسجيل الدخول", "登录"));
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = centerLabel(t(lang, "Gebruikersnaam", "Username", "اسم المستخدم", "用户名"), 16);
        JTextField user = new JTextField();
        setFieldSize(user, 420, 40);

        JLabel passLabel = centerLabel(t(lang, "Wachtwoord", "Password", "كلمة المرور", "密码"), 16);
        JPasswordField pass = new JPasswordField();
        setFieldSize(pass, 420, 40);

        JButton btn = createButton(t(lang, "Login", "Login", "دخول", "登录"), 18);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFieldSize(btn, 220, 45);

        JLabel msg = new JLabel("");
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setFont(new Font("Arial", Font.PLAIN, 14));

        form.add(title);
        form.add(Box.createVerticalStrut(25));
        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(user);
        form.add(Box.createVerticalStrut(14));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(pass);
        form.add(Box.createVerticalStrut(22));
        form.add(btn);
        form.add(Box.createVerticalStrut(12));
        form.add(msg);

        btn.addActionListener(e -> {
            String username = user.getText().trim();
            String password = new String(pass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                msg.setText(t(lang, "Vul alles in.", "Fill in everything.", "يرجى تعبئة جميع الحقول.", "请填写所有内容。"));
                return;
            }

            // TODO DB: SELECT * FROM users WHERE username=? AND password=?
            currentUser = username;
            openPage(frame, () -> showStartPage(lang));
        });

        frame.add(wrapper, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // ==================== USER REGISTER ====================
    public static void showUserRegister(String lang) {
        JFrame frame = createBaseFrame(t(lang, "Account maken", "Register", "إنشاء حساب", "注册"), lang);

        JPanel wrapper = createCenteredFormPanel();
        JPanel form = (JPanel) wrapper.getComponent(0);

        JLabel title = new JLabel(t(lang, "Account aanmaken", "Create account", "إنشاء حساب", "创建账号"));
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = centerLabel(t(lang, "Gebruikersnaam", "Username", "اسم المستخدم", "用户名"), 16);
        JTextField user = new JTextField();
        setFieldSize(user, 420, 40);

        JLabel passLabel = centerLabel(t(lang, "Wachtwoord", "Password", "كلمة المرور", "密码"), 16);
        JPasswordField pass = new JPasswordField();
        setFieldSize(pass, 420, 40);

        JLabel pass2Label = centerLabel(t(lang, "Herhaal wachtwoord", "Repeat password", "أعد كلمة المرور", "重复密码"), 16);
        JPasswordField pass2 = new JPasswordField();
        setFieldSize(pass2, 420, 40);

        JButton btn = createButton(t(lang, "Registreren", "Register", "تسجيل", "注册"), 18);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFieldSize(btn, 220, 45);

        JLabel msg = new JLabel("");
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setFont(new Font("Arial", Font.PLAIN, 14));

        form.add(title);
        form.add(Box.createVerticalStrut(25));
        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(user);
        form.add(Box.createVerticalStrut(14));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(pass);
        form.add(Box.createVerticalStrut(14));
        form.add(pass2Label);
        form.add(Box.createVerticalStrut(6));
        form.add(pass2);
        form.add(Box.createVerticalStrut(22));
        form.add(btn);
        form.add(Box.createVerticalStrut(12));
        form.add(msg);

        btn.addActionListener(e -> {
            String username = user.getText().trim();
            String p1 = new String(pass.getPassword()).trim();
            String p2 = new String(pass2.getPassword()).trim();

            if (username.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                msg.setText(t(lang, "Vul alles in.", "Fill in everything.", "يرجى تعبئة جميع الحقول.", "请填写所有内容。"));
                return;
            }
            if (!p1.equals(p2)) {
                msg.setText(t(lang, "Wachtwoorden komen niet overeen.", "Passwords do not match.", "كلمات المرور غير متطابقة.", "两次密码不一致。"));
                return;
            }

            // TODO DB: INSERT INTO users(username,password,created_at) VALUES(?,?,NOW())
            msg.setText(t(lang, "Account gemaakt (demo).", "Account created (demo).", "تم إنشاء الحساب (تجريبي).", "账号已创建（演示）。"));

            currentUser = username;
            openPage(frame, () -> showStartPage(lang));
        });

        frame.add(wrapper, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // ==================== ADMIN ====================
    public static void showAdminLoginOrRegister(String lang) {
        JFrame frame = createBaseFrame("Admin", lang);

        JPanel wrapper = createCenteredFormPanel();
        JPanel form = (JPanel) wrapper.getComponent(0);

        JLabel title = new JLabel("Admin");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton login = createButton("Login", 18);
        JButton register = createButton("Register", 18);
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        register.setAlignmentX(Component.CENTER_ALIGNMENT);

        setFieldSize(login, 220, 45);
        setFieldSize(register, 220, 45);

        form.add(title);
        form.add(Box.createVerticalStrut(30));
        form.add(login);
        form.add(Box.createVerticalStrut(15));
        form.add(register);

        login.addActionListener(e -> openPage(frame, () -> showAdminLogin(lang)));
        register.addActionListener(e -> openPage(frame, () -> showAdminRegistration(lang)));

        frame.add(wrapper, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void showAdminRegistration(String lang) {
        JFrame frame = createBaseFrame("Admin Registration", lang);

        JPanel wrapper = createCenteredFormPanel();
        JPanel form = (JPanel) wrapper.getComponent(0);

        JLabel title = new JLabel("Admin Registration");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = centerLabel("Username", 16);
        JTextField user = new JTextField();
        setFieldSize(user, 420, 40);

        JLabel emailLabel = centerLabel("Email", 16);
        JTextField email = new JTextField();
        setFieldSize(email, 420, 40);

        JLabel passLabel = centerLabel("Password", 16);
        JPasswordField pass = new JPasswordField();
        setFieldSize(pass, 420, 40);

        JButton btn = createButton("Register", 18);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFieldSize(btn, 220, 45);

        JLabel msg = new JLabel("");
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setFont(new Font("Arial", Font.PLAIN, 14));

        form.add(title);
        form.add(Box.createVerticalStrut(25));
        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(user);
        form.add(Box.createVerticalStrut(14));
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(email);
        form.add(Box.createVerticalStrut(14));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(pass);
        form.add(Box.createVerticalStrut(22));
        form.add(btn);
        form.add(Box.createVerticalStrut(12));
        form.add(msg);

        btn.addActionListener(e -> {
            // TODO DB: INSERT INTO admins(username,email,password) VALUES (?,?,?)
            msg.setText("Admin registered (demo).");
        });

        frame.add(wrapper, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void showAdminLogin(String lang) {
        JFrame frame = createBaseFrame("Admin Login", lang);

        JPanel wrapper = createCenteredFormPanel();
        JPanel form = (JPanel) wrapper.getComponent(0);

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = centerLabel("Username", 16);
        JTextField user = new JTextField();
        setFieldSize(user, 420, 40);

        JLabel passLabel = centerLabel("Password", 16);
        JPasswordField pass = new JPasswordField();
        setFieldSize(pass, 420, 40);

        JButton btn = createButton("Login", 18);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFieldSize(btn, 220, 45);

        JLabel msg = new JLabel("");
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setFont(new Font("Arial", Font.PLAIN, 14));

        form.add(title);
        form.add(Box.createVerticalStrut(25));
        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(user);
        form.add(Box.createVerticalStrut(14));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(pass);
        form.add(Box.createVerticalStrut(22));
        form.add(btn);
        form.add(Box.createVerticalStrut(12));
        form.add(msg);

        btn.addActionListener(e -> {
            // TODO DB: SELECT * FROM admins WHERE username=? AND password=?
            openPage(frame, () -> showAdminDashboard(lang));
        });

        frame.add(wrapper, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void showAdminDashboard(String lang) {
        JFrame frame = createBaseFrame("Admin Dashboard", lang);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 18));

        // TODO DB: SELECT * FROM helpvragen ORDER BY datum DESC
        area.setText("Admin Dashboard (demo)\n\nHier komen later de helpvragen uit MySQL.");

        frame.add(new JScrollPane(area), BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
