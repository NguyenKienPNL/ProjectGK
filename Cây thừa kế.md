classDiagram
direction BT
class Balloom {
  + update() void
  + move() void
}
class Bomb {
  + exploded() void
  + isExploded() boolean
  - animate() void
  + setCanWalkThrough(boolean) void
  + isCanWalkThrough() boolean
  + update() void
  + getOwner() Bomber
  - initAnimation() void
}
class BombItem {
  + applyEffect(Bomber) void
}
class Bomber {
  + decreaseFlameLength() void
  + addScore(int) void
  # align(int, int) void
  + decreaseSpeed() void
  + setCurrentLevel(int) void
  + increaseSpeed() void
  + setBombSounds(AudioClip, AudioClip) void
  + increaseFlameLength() void
  + getCurrentLevel() int
  + setSpeedBufftime(int) void
  + getBombCount() int
  + update() void
  + decreaseBomb() void
  + setBombBufftime(int) void
  + setFlameBufftime(int) void
  + getScore() int
  + increaseBomb() void
  # moveWithCollision(int, int) boolean
  + destroy() void
  + setGameInstance(BombermanGame) void
  + getBombRadius() int
  + setBombRadius(int) void
  + placeBomb() void
  + setBombCount(int) void
  + handleKeyEvent(Scene) void
  + isDead() boolean
}
class Brick {
  + isDestroyed() boolean
  + update() void
  + destroy() void
}
class Enemy {
  + destroy() void
  + move() void
  + update() void
  + isDead() boolean
  + getPoint() int
}
class Entity {
  + setRealY(int) void
  + render(GraphicsContext) void
  + getRealX() int
  + setX(int) void
  + getYFromRealY(int) int
  + getNextUpImage() Image
  + getY() int
  + setImg(Image) void
  + getImg() Image
  + update() void
  + setY(int) void
  + setRealX(int) void
  + getRealY() int
  + getNextDownImage() Image
  # moveWithCollision(int, int) boolean
  # align(int, int) void
  + getNextLeftImage() Image
  + getX() int
  + getNextRightImage() Image
  + getXFromRealX(int) int
}
class Flame {
  + update() void
  + getFrame(int) void
  + isFinished() boolean
}
class FlameItem {
  # getImageFrame2() Image
  # getImageFrame1() Image
  + applyEffect(Bomber) void
}
class FlameSegments {
  - addDirection(int, int, int) void
  + getFlames() List~Flame~
  + getImages(int, boolean) List~Image~
  + update() void
  + destroy() void
  + isFinished() boolean
}
class Grass {
  + isCanPlaceBomb() boolean
  + getSymbol() char
  + isCanWalkThrough() boolean
  + update() void
}
class Item {
  # getImageFrame1() Image
  + isPickedUp() boolean
  + applyEffect(Bomber) void
  # getImageFrame2() Image
  + pickUp() void
  + update() void
}
class ItemDistribute {
  + update() void
  + findAvailablePoints() void
  + addAPowerUp() void
}
class Oneal {
  + chooseARandomDirection() void
  + isCentered() boolean
  + move() void
  + update() void
  ~ distance() int
  + chooseDirection() void
  + nearToBomberman() boolean
}
class Point
class Portal {
  + update() void
  + isHidden() boolean
  + setHidden(boolean) void
}
class SpeedItem {
  + applyEffect(Bomber) void
}
class Wall {
  + getSymbol() char
  + update() void
}

Balloom  -->  Enemy 
Bomb  -->  Entity 
BombItem  -->  Item 
Bomber  -->  Entity 
Brick  -->  Entity 
Enemy  -->  Entity 
Flame  -->  Entity 
FlameItem  -->  Item 
FlameSegments  -->  Entity 
Grass  -->  Entity 
Item  -->  Entity 
ItemDistribute  -->  Entity 
Oneal  -->  Enemy 
ItemDistribute  -->  Point 
Portal  -->  Entity 
SpeedItem  -->  Item 
Wall  -->  Entity 
