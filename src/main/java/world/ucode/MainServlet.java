package world.ucode;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@WebServlet("/gnida")
@MultipartConfig
public class MainServlet extends HttpServlet {
    BufferedImage image;
    String format;
    String[] img_type = {"jpg","png","gif","bmp","tiff","jpeg"};

    BufferedImage makeGrey(BufferedImage buffImage) {
        BufferedImage grey = new BufferedImage(buffImage.getWidth(), buffImage.getHeight(), buffImage.getType());

        for (int a = 0; a < buffImage.getWidth(); a++) {
            for (int b = 0; b < buffImage.getHeight(); b++) {
                Color c = new Color(buffImage.getRGB(a, b));
                int Cgrey = (int)(c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
                Color newC = new Color(Cgrey, Cgrey, Cgrey);
                grey.setRGB(a, b, newC.getRGB());
            }
        }

        return grey;
    }

    BufferedImage makeInversed(BufferedImage buffImage) {
        BufferedImage inverse = new BufferedImage(buffImage.getWidth(), buffImage.getHeight(), buffImage.getType());
        Color c;

        for (int a = 0; a < buffImage.getWidth(); a++) {
            for (int b = 0; b < buffImage.getHeight(); b++) {
                c = new Color(buffImage.getRGB(a, b));
                Color newC = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
                inverse.setRGB(a, b, newC.getRGB());
            }
        }

        return inverse;
    }

    BufferedImage makePixelized(BufferedImage buffImage, int pix_n) {
        BufferedImage simple = new BufferedImage(buffImage.getWidth(), buffImage.getHeight(), buffImage.getType());
        Color c;

        for (int a = 0; a < buffImage.getWidth(); a = a + pix_n) {
            for (int b = 0; b < buffImage.getHeight(); b = b + pix_n) {
                c = new Color(buffImage.getRGB(a, b));

                for (int x = a; x < a + pix_n && x < buffImage.getWidth(); x++) {
                    for (int y = b; y < b + pix_n && y < buffImage.getHeight(); y++) {
                        simple.setRGB(x, y, c.getRGB());
                    }
                }
            }
        }

        return simple;
    }

    BufferedImage makeTriangle(BufferedImage buffImage, int pix_n) {
        BufferedImage triangle = new BufferedImage(buffImage.getWidth(), buffImage.getHeight(), buffImage.getType());
        Color c;
        Color e;
        int W = buffImage.getWidth();
        int H = buffImage.getHeight();

        try {
            for (int a = 0; a < W; a = a + pix_n) {
                for (int b = 0; b < H; b = b + pix_n) {
                    if(b + pix_n - 1 < H) {
                        c = new Color(buffImage.getRGB(a, b + pix_n - 1));
                    } else {
                        c = new Color(buffImage.getRGB(a, H - 1));
                    }

                    if(a + pix_n - 1 < W) {
                        e = new Color(buffImage.getRGB(a + pix_n - 1, b));
                    } else {
                        e = new Color(buffImage.getRGB(W - 1, b));
                    }

                    for (int x = a; x < a + pix_n && x < W; x++) {
                        for (int y = b; y < b + pix_n && y < H; y++) {
                            if(y - b <= x - a) {
                                triangle.setRGB(x, y, c.getRGB());
                            } else {
                                triangle.setRGB(x, y, e.getRGB());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex){
            System.out.println(ex);
        }

        return triangle;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            boolean correct = false;
            Part part = request.getPart("image");
            int pix_n = Integer.parseInt(request.getParameter("pix_n"));
            int pix_type = Integer.parseInt(request.getParameter("pix_type"));
            int filter_type= Integer.parseInt(request.getParameter("filter_type"));
            InputStream file = part.getInputStream();
            image = ImageIO.read(file);
            String Name = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            int r = Name.lastIndexOf(".");
            format = Name.substring(r + 1);

            for(int y = 0; y < 6; y++) {
                if (format.equals(img_type[y])) {
                    correct = true;
                    break;
                }
            }

            if (correct) {
                //writeDB();
                ServletOutputStream os = response.getOutputStream();

                if(filter_type == 1) {
                    image = makeGrey(image);
                } else if(filter_type == 2) {
                    image = makeInversed(image);
                }

                if(pix_type == 0) {
                    image = makePixelized(image, pix_n);
                } else if(pix_type == 1) {
                    image = makeTriangle(image, pix_n);
                }

                ImageIO.write(image, format, os);
            }
        }
        catch(Exception excep){
            System.out.println(excep);
        }

    }
}
