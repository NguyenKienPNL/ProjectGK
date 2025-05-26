package uet.oop.bomberman.entities;

import javafx.scene.canvas.GraphicsContext; // Cần import để render
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public class Portal extends Entity {

    private boolean isHidden; // Biến trạng thái ẩn/hiện của Portal

    public Portal(int x, int y, Image img, boolean isHidden) { // THAY ĐỔI: Thêm tham số isHidden
        super(x, y, img);
        this.isHidden = isHidden; // Khởi tạo trạng thái ban đầu
    }

    @Override
    public void update() {
        // Portal không cần update phức tạp
    }

    @Override
    public void render(GraphicsContext gc) { // THAY ĐỔI: Override phương thức render
        if (!isHidden) { // Chỉ render nếu không bị ẩn
            super.render(gc); // Gọi phương thức render của lớp cha (Entity)
        }
    }

    public boolean isHidden() { // Getter để kiểm tra trạng thái ẩn
        return isHidden;
    }

    public void setHidden(boolean hidden) { // Setter để thay đổi trạng thái ẩn
        this.isHidden = hidden;
    }
}