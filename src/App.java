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
    String foxy = "";
    long nextFoxy = 0;
    long nextFoxyJumpscare = 0;
    int time = 12;
    String jumpscare = "";
    double musicBoxProgress = 100;

    final Rectangle foxySpawn;
    public App () throws IOException {
        // Set constants
        multiplier = Toolkit.getDefaultToolkit().getScreenSize().width / 1920;
        foxySpawn = new Rectangle(798, 268, 340, 340);
        cameraImages[0] = ImageIO.read(getClass().getResource("cam0.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[1] = ImageIO.read(getClass().getResource("cam1.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[2] = ImageIO.read(getClass().getResource("cam2.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[3] = ImageIO.read(getClass().getResource("cam3.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        cameraImages[4] = ImageIO.read(getClass().getResource("cam4.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);

        for (int i = 0; i < maskImages.length; i++) {
            maskImages[i] = ImageIO.read(getClass().getResource(i+1 + ".png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH);
        }

        // Load images
        images.put("office", ImageIO.read(getClass().getResource("office2.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH));
        images.put("office_flashlighted", ImageIO.read(getClass().getResource("office2_flashlighted.png")).getScaledInstance((int)(1980 * multiplier), (int)(1080 * multiplier), Image.SCALE_SMOOTH));
        images.put("flashlight", ImageIO.read(getClass().getResource("flashlight.png")));
        images.put("andrew", ImageIO.read(getClass().getResource("andrew.png")));
        images.put("deev", ImageIO.read(getClass().getResource("deev.png")));
        images.put("don", ImageIO.read(getClass().getResource("don.png")));
        images.put("ethan", ImageIO.read(getClass().getResource("ethan.png")));
        images.put("joseph", ImageIO.read(getClass().getResource("joseph.png")));
        images.put("kairo", ImageIO.read(getClass().getResource("kairo.png")));
        images.put("katie", ImageIO.read(getClass().getResource("katie.png")));
        images.put("mk", ImageIO.read(getClass().getResource("mk.png")));
        images.put("steph", ImageIO.read(getClass().getResource("steph.png")));

        // Set up JPanel
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Arial", Font.BOLD, (int)(40 * multiplier)));
                g.setColor(Color.WHITE);
                if (currentCamera == -1) {
                    if (flashlight) {
                        g.drawImage(images.get("office_flashlighted"), 0, 0, this);
                        g.drawImage(images.get("flashlight"), (int)((foxySpawn.x-70) * multiplier), (int)((foxySpawn.y-70) * multiplier), (int)((foxySpawn.width+140) * multiplier), (int)((foxySpawn.height+140) * multiplier), null);
                        g.drawImage(images.get(foxy), (int)(foxySpawn.x * multiplier), (int)(foxySpawn.y * multiplier), (int)(foxySpawn.width * multiplier), (int)(foxySpawn.height * multiplier), null);    
                    } else {
                        g.drawImage(images.get("office"), 0, 0, this);
                    }
                    g.drawString(time + " AM", (int)(10*multiplier), (int)(50*multiplier));
                } else {
                    g.drawImage(cameraImages[currentCamera], 0, 0, this);
                    if (currentCamera == 0 && musicBoxProgress > 0) {
                        g.drawRect((int)(10*multiplier), (int)(10*multiplier), (int)(4*100*multiplier), (int)(20*multiplier));
                        g.fillRect((int)(10*multiplier), (int)(10*multiplier), (int)(4*musicBoxProgress*multiplier), (int)(20*multiplier));
                    }
                }
                if (maskIndex > -1) {
                    g.drawImage(maskImages[maskIndex], 0, 0, this);
                }

                if (jumpscare != "") {
                    g.drawImage(images.get(jumpscare), 420, 0, 1080, 1080, null);
                }
            }
        };

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!mask && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (currentCamera == -1) {
                        flashlight = true;
                    } else if (currentCamera == 0) {
                        if (musicBoxProgress < 100) {
                            musicBoxProgress += 0.2;
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_1) {
                    if (currentCamera == 0) {
                        currentCamera = -1;
                        sounds.get("musicBox").stop();
                    } else {
                        flashlight = false;
                        currentCamera = 0;
                        playSound("musicBox");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_2) {
                    if (currentCamera == 1) {
                        currentCamera = -1;
                    } else {
                        flashlight = false;
                        currentCamera = 1;
                        sounds.get("musicBox").stop();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_3) {
                    if (currentCamera == 2) {
                        currentCamera = -1;
                    } else {
                        flashlight = false;
                        currentCamera = 2;
                        sounds.get("musicBox").stop();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_4) {
                    if (currentCamera == 3) {
                        currentCamera = -1;
                    } else {
                        flashlight = false;
                        currentCamera = 3;
                        sounds.get("musicBox").stop();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_5) {
                    if (currentCamera == 4) {
                        currentCamera = -1;
                    } else {
                        flashlight = false;
                        currentCamera = 4;
                        sounds.get("musicBox").stop();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_S && currentCamera == -1) {
                    flashlight = false;
                    if (mask) {
                        mask = false;
                    } else {
                        maskIndex = 0;
                        mask = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (currentCamera == -1 && e.getKeyCode() == KeyEvent.VK_SPACE) {
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
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(name + ".wav")));
            clip.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (clip.getFramePosition() < clip.getFrameLength()) {
                        Thread.yield();
                    }
                    clip.close();
                    if (name.equals("weasel")) {
                        jumpscare = "mk";
                    }
                }
            }).start();
            sounds.put(name, clip);
        } catch (NullPointerException e) {
            System.out.println("audio file not found: " + name);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e1) {
            e1.printStackTrace();
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
                    dispose();
                }

                if (mask && maskIndex < maskImages.length-1) {
                    maskIndex++;
                } else if (!mask && maskIndex < maskImages.length && maskIndex >= 0) {
                    maskIndex--;
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
                        mask = false;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}