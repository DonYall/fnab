import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;

public class App extends JFrame {
    final double multiplier;
    final JPanel gamePanel;
    final HashMap<String, Image> images = new HashMap<String, Image>();
    final HashMap<String, Clip> sounds = new HashMap<String, Clip>();
    final Image[] cameraImages = new Image[5];
    final Image[] maskImages = new Image[15];
    int currentCamera = -1;
    boolean flashlight = false;
    boolean mask = false;
    int maskIndex = -1;
    double flashlightBattery = 100;
    String foxy = "";
    long nextFoxy = 0;
    long nextFoxyJumpscare = 0;
    int time = 12;
    String jumpscare = "";
    double musicBoxProgress = 100;
    int josephPosition = 2;
    long nextJosephMovement = 0;
    int pearheadPosition = -2;
    long nextPearheadMovement = 0;
    boolean pearheadInOffice = false;
    long nextKatieJumpscare = 0;
    boolean katieInOffice = false;
    long nextAndrew = 0;
    long nextAndrewJumpscare = 0;
    boolean floorVent = false;
    final Rectangle foxySpawn;
    final Rectangle cameraSpawn;

    public App () throws IOException {
        // Set constants
        multiplier = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 1920;
        foxySpawn = new Rectangle(798, 268, 340, 340);
        cameraSpawn = new Rectangle(-50, -50, 1200, 1200);
        cameraImages[0] = ImageIO.read(getClass().getResource("Assets/cam0.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[1] = ImageIO.read(getClass().getResource("Assets/cam1.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[2] = ImageIO.read(getClass().getResource("Assets/cam2.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[3] = ImageIO.read(getClass().getResource("Assets/cam3.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[4] = ImageIO.read(getClass().getResource("Assets/cam4.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);

        for (int i = 0; i < maskImages.length; i++) {
            maskImages[i] = ImageIO.read(getClass().getResource("Assets/" + (i+1) + ".png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        }

        // Load images
        images.put("office", ImageIO.read(getClass().getResource("Assets/office2.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH));
        images.put("office_flashlighted", ImageIO.read(getClass().getResource("Assets/office2_flashlighted.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH));
        images.put("officeFloor", ImageIO.read(getClass().getResource("Assets/officeFloor.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH));
        images.put("flashlight", ImageIO.read(getClass().getResource("Assets/flashlight.png")));
        images.put("andrew", ImageIO.read(getClass().getResource("Assets/andrew.png")));
        // images.put("deev", ImageIO.read(getClass().getResource("Assets/deev.png")));
        // images.put("don", ImageIO.read(getClass().getResource("Assets/don.png")));
        images.put("ethan", ImageIO.read(getClass().getResource("Assets/ethan.png")));
        images.put("joseph", ImageIO.read(getClass().getResource("Assets/joseph.png")));
        // images.put("kairo", ImageIO.read(getClass().getResource("Assets/kairo.png")));
        images.put("katie", ImageIO.read(getClass().getResource("Assets/katie.png")));
        images.put("katieGhost", ImageIO.read(getClass().getResource("Assets/katieGhost.png")));
        images.put("mk", ImageIO.read(getClass().getResource("Assets/mk.png")));
        images.put("pearhead", ImageIO.read(getClass().getResource("Assets/pearhead.png")));
        // images.put("steph", ImageIO.read(getClass().getResource("Assets/steph.png")));

        // Set up JPanel
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                if (currentCamera == -1) {
                    if (flashlight) {
                        flashlightBattery -= 0.2;
                        if (flashlightBattery <= 0) {
                            flashlight = false;
                        }
                        g.drawImage(images.get("office_flashlighted"), 0, 0, this);
                        g.drawImage(images.get("flashlight"), (int)((foxySpawn.x-70) * multiplier), (int)((foxySpawn.y-70) * multiplier), (int)((foxySpawn.width+140) * multiplier), (int)((foxySpawn.height+140) * multiplier), null);
                        g.drawImage(images.get(foxy), (int)(foxySpawn.x * multiplier), (int)(foxySpawn.y * multiplier), (int)(foxySpawn.width * multiplier), (int)(foxySpawn.height * multiplier), null);    
                    } else {
                        g.drawImage(images.get("office"), 0, 0, this);
                    }

                    if (!floorVent) {
                        g.drawImage(images.get("officeFloor"), 0, 0, this);
                    } else {
                        flashlightBattery -= 0.2;
                    }

                    if (pearheadInOffice) {
                        g.drawImage(images.get("pearhead"), 0, (int)((590) * multiplier), (int)(590 * multiplier), (int)(590 * multiplier), null);
                    }

                    if (katieInOffice) {
                        g.drawImage(images.get("katieGhost"), (int)(foxySpawn.x * multiplier), (int)(foxySpawn.y * multiplier), (int)(foxySpawn.width * multiplier), (int)(foxySpawn.height * multiplier), null);
                    }

                    if (maskIndex > -1) {
                        g.drawImage(maskImages[maskIndex], 0, 0, this);
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, (int)(10 * multiplier)));
                    g.drawString("Battery: ", (int)((1920 - 110)*multiplier), (int)(20*multiplier));
                    if (flashlightBattery > 0) g.fillRect((int)((1920 - 110)*multiplier), (int)(30*multiplier), (int)(((int)((flashlightBattery + 24)/25))*25*multiplier), (int)(20*multiplier));
                    g.setFont(new Font("Arial", Font.BOLD, (int)(40 * multiplier)));
                    g.drawString(time + " AM", (int)(10*multiplier), (int)(50*multiplier));
                } else {
                    g.drawImage(cameraImages[currentCamera], 0, 0, this);
                    if (currentCamera == 0 && musicBoxProgress > 0) {
                        g.drawRect((int)(10*multiplier), (int)(10*multiplier), (int)(4*100*multiplier), (int)(20*multiplier));
                        g.fillRect((int)(10*multiplier), (int)(10*multiplier), (int)(4*musicBoxProgress*multiplier), (int)(20*multiplier));
                    } else if (currentCamera == 0) {
                        g.drawImage(images.get("mk"), (int)(foxySpawn.x * multiplier), (int)(foxySpawn.y * multiplier), (int)(foxySpawn.width * multiplier), (int)(foxySpawn.height * multiplier), null);
                    }

                    if (josephPosition == currentCamera) {
                        g.drawImage(images.get("joseph"), (int)(cameraSpawn.x * multiplier), (int)(cameraSpawn.y * multiplier), (int)(cameraSpawn.width * multiplier), (int)(cameraSpawn.height * multiplier), null);
                    }

                    if (pearheadPosition == currentCamera) {
                        g.drawImage(images.get("pearhead"), (int)(cameraSpawn.x * multiplier), (int)(cameraSpawn.y * multiplier), (int)(cameraSpawn.width * multiplier), (int)(cameraSpawn.height * multiplier), null);
                    }
                }

                if (jumpscare != "") {
                    g.drawImage(images.get(jumpscare), (int)(420 * multiplier), 0, (int)(1080 * multiplier), (int)(1080 * multiplier), null);
                }
            }
        };

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!flashlight && !mask && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (currentCamera == -1 && flashlightBattery > 0) {
                        flashlight = true;
                        playSound("flashlight");
                    } else if (currentCamera == 0) {
                        if (musicBoxProgress < 100) {
                            musicBoxProgress += 0.3;
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_1) {
                    if (mask) return;
                    katieInOffice = false;
                    if (currentCamera == 0) {
                        currentCamera = -1;
                        sounds.get("musicBox").stop();
                        playSound("blip");
                        if ((int) (Math.random() * 10) == 0) {
                            katieInOffice = true;
                            nextKatieJumpscare = System.currentTimeMillis() + 1500;
                        }
                    } else {
                        flashlight = false;
                        currentCamera = 0;
                        if (musicBoxProgress > 0) playSound("musicBox");
                        playSound("blip");
                    }
                } else if (e.getKeyCode() >= KeyEvent.VK_2 && e.getKeyCode() <= KeyEvent.VK_5) {
                    if (mask) return;
                    katieInOffice = false;
                    if (currentCamera == e.getKeyCode()-49) {
                        currentCamera = -1;
                        playSound("blip");
                        if ((int) (Math.random() * 10) == 0) {
                            katieInOffice = true;
                            nextKatieJumpscare = System.currentTimeMillis() + 1500;
                        }
                    } else {
                        flashlight = false;
                        if (currentCamera == 0) sounds.get("musicBox").stop();
                        currentCamera = e.getKeyCode()-49;
                        playSound("blip");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_S && currentCamera == -1) {
                    flashlight = false;
                    if (mask) {
                        mask = false;
                    } else {
                        maskIndex = 0;
                        mask = true;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_W && currentCamera == -1 && !mask) {
                    floorVent = !floorVent;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (flashlight && currentCamera == -1 && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    flashlight = false;
                }
            }
        });

        playSound("ambience");
        add(gamePanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        startGame();
    }

    void playSound(String name) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource("Assets/" + name + ".wav")));
            clip.start();
            if (name.equals("weasel")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (clip.getFramePosition() < clip.getFrameLength()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        clip.close();
                    jumpscare = "mk";
                    currentCamera = -1;
                    mask = false;
                    maskIndex = -1;
                    }
                }).start();
            }
            sounds.put(name, clip);
        } catch (NullPointerException e) {
            System.out.println("audio file not found: " + name);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e1) {
            System.out.println(e1.getMessage());
        }
    }

    void startGame() {
        final long startTime = System.currentTimeMillis();
        while (true) {
            try {
                time = (int) (((System.currentTimeMillis() - startTime) / 1000) / 60);
                if (time == 0) {
                    time = 12;
                }
                if (time == 6) {
                    playSound("6am");
                    currentCamera = -1;
                    jumpscare = "";
                    mask = false;
                    maskIndex = -1;
                    flashlight = false;
                    repaint();
                    break;
                } else {
                    if (mask && maskIndex < maskImages.length-1) {
                        maskIndex++;
                    } else if (!mask && maskIndex < maskImages.length && maskIndex >= 0) {
                        maskIndex--;
                    }

                    if (nextJosephMovement == 0) {
                        nextJosephMovement = System.currentTimeMillis() + (1000 * 60);
                    } else if (nextJosephMovement < System.currentTimeMillis()) {
                        if (josephPosition == 0) {
                            josephPosition = (int) (Math.random() * 2) + 3;
                        } else if (josephPosition > 2) {
                            if (!mask) {
                                jumpscare = "joseph";
                                currentCamera = -1;
                                maskIndex = -1;    
                            } else {
                                josephPosition = (int) (Math.random() * 5);
                            }
                        } else {
                            josephPosition = (int) (Math.random() * 3);
                        }
                        nextJosephMovement = System.currentTimeMillis() + (1000 * 30);
                    }

                    if (nextPearheadMovement == 0) {
                        nextPearheadMovement = System.currentTimeMillis() + (2000 * 60);
                    } else if (nextPearheadMovement < System.currentTimeMillis() && pearheadPosition == -2) {
                        pearheadPosition = (int) (Math.random() * 2) + 3;
                        nextPearheadMovement = System.currentTimeMillis() + (1000 * 30);
                    } else if (nextPearheadMovement < System.currentTimeMillis() && pearheadPosition >= 3 && pearheadPosition < 5) {
                        if (!mask) {
                            flashlightBattery = 0;
                            pearheadPosition = 5;
                            pearheadInOffice = true;
                            playSound("pearhead");    
                        } else {
                            pearheadPosition = -2;
                            nextPearheadMovement = System.currentTimeMillis() + (1000 * 60);
                        }
                    }

                    if (nextKatieJumpscare < System.currentTimeMillis() && katieInOffice) {
                        jumpscare = "katie";
                        currentCamera = -1;
                        maskIndex = -1;
                        katieInOffice = false;
                    }

                    if (nextAndrew == 0) {
                        nextAndrew = System.currentTimeMillis() + (int) (Math.random() * 10000) + 10000;
                    } else if (nextAndrew < System.currentTimeMillis()) {
                        nextAndrew = System.currentTimeMillis() + (int) (Math.random() * 10000) + 13000;
                        playSound("vents");
                        nextAndrewJumpscare = System.currentTimeMillis() + (1000 * 3);
                    }

                    if (nextAndrewJumpscare != 0 && nextAndrewJumpscare < System.currentTimeMillis()) {
                        if (floorVent) {
                            nextAndrewJumpscare = 0;
                        } else {
                            jumpscare = "andrew";
                            currentCamera = -1;
                            maskIndex = -1;
                        }
                    }

                    if (nextFoxy == 0 && foxy == "") {
                        nextFoxy = System.currentTimeMillis() + (int) (Math.random() * 20000) + 10000;
                    } else if (nextFoxy < System.currentTimeMillis() && foxy == "") {
                        foxy = "ethan";
                        nextFoxy = 0;
                        nextFoxyJumpscare = System.currentTimeMillis() + (int) (Math.random() * 10000) + 5000;
                    } else if (nextFoxyJumpscare < System.currentTimeMillis() && foxy == "ethan") {
                        if (mask) {
                            nextFoxyJumpscare = 0;
                            foxy = "";
                            nextFoxy = System.currentTimeMillis() + (int) (Math.random() * 20000) + 10000;
                        } else {
                            jumpscare = foxy;
                            currentCamera = -1;
                            maskIndex = -1;
                        }
                    }

                    musicBoxProgress -= 0.1;
                    if (musicBoxProgress < 0 && musicBoxProgress > -1) {
                        playSound("weasel");
                        musicBoxProgress = -1;
                    } else if (musicBoxProgress > 100) {
                        musicBoxProgress = 100;
                    }

                    Thread.sleep(1000 / 30);
                    revalidate();
                    repaint();
                    if (jumpscare != "") {
                        playSound("anitaScream");
                        break;
                    }    
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}