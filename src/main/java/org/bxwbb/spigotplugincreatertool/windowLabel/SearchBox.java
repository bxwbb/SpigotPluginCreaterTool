package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.ClassAnalyzer;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.JavaSourceScannerFixed;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.NodeCreater;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchBox extends BaseLabel {

    public static final double TEXT_HEIGHT = 20;

    public Rectangle background;
    public TextField textField;
    public Button searchButton;
    public List<JavaSourceScannerFixed.ClassInfo> searchResultRecords;
    public static List<JavaSourceScannerFixed.ClassInfo> records = new ArrayList<>();
    public ClassAnalyzer.ClassInfo records2;
    public ClassAnalyzer.ClassInfo records2Copy;
    public final List<Text> searchResultTexts = new ArrayList<>();
    public final List<Button[]> searchResultButtons = new ArrayList<>();
    public final List<SearchType> searchResultType = new ArrayList<>();
    public boolean isFunction = false;

    private String lastSearchText = "";

    public SearchBox(double x, double y, double screenWidth, double screenHeight) {
        this.startX = x;
        this.startY = y;
        this.endX = screenWidth - 5;
        this.endY = y + ((int) ((screenHeight - y - 0) / TEXT_HEIGHT)) * TEXT_HEIGHT;
        this.base = new Group();
        this.background = new Rectangle(x, y, this.endX - this.startX, this.endY - this.startY);
        background.setFill(HelloApplication.BG_COLOR);
        background.setArcWidth(HelloApplication.ROUNDNESS);
        background.setArcHeight(HelloApplication.ROUNDNESS);
        background.setStrokeWidth(1.0);
        background.setStroke(HelloApplication.BORDER_COLOR);
        this.textField = new TextField("");
        this.textField.requestFocus();
        this.textField.setFont(HelloApplication.TEXT_FONT);
        this.textField.setStyle(
                "-fx-background-color: " + HelloApplication.toHexString(HelloApplication.BG_COLOR) + ";" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: text;" +
                        "-fx-text-fill: " + HelloApplication.toHexString(HelloApplication.FONT_COLOR) + ";"
        );
        this.textField.setLayoutX(startX + 5);
        this.textField.setLayoutY(startY + 7);
        this.textField.setPrefWidth(endX - startX - 30);
        this.searchButton = new Button(endX - 25, startY + 5, endX - 5, startY + 25, true);
        this.searchButton.resetImage(
                new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Close.png"
                )))
        );
        this.searchButton.background.setOnMouseClicked(event1 -> {
            if (this.isFunction) {
                this.textField.setText(this.lastSearchText);
                this.searchResultRecords = records;
                this.searchResultRecords = this.searchResultRecords.stream()
                        .filter(item -> item.fullClassName().contains(this.textField.getText()))
                        .collect(Collectors.toList());
                this.searchButton.resetImage(
                        new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(
                                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Close.png"
                        )))
                );
            } else {
                this.delete();
                HelloApplication.cancelLabel = null;
            }
            this.isFunction = !this.isFunction;
            updateTexts();
        });
        for (int i = 0; i < ((int) ((screenHeight - y - 40) / TEXT_HEIGHT)); i++) {
            this.searchResultTexts.add(new Text("测试Text"));
            this.searchResultTexts.get(i).setX(startX + 85);
            this.searchResultTexts.get(i).setY(startY + 40 + i * TEXT_HEIGHT);
            this.searchResultTexts.get(i).setFill(HelloApplication.SELECTED_COLOR);
            final int finalI = i;
            this.searchResultTexts.get(i).setOnMouseClicked(event -> {
                try {
                    if (!isFunction) {
                        final String className = this.searchResultTexts.get(finalI).getText();
                        isFunction = true;
                        this.lastSearchText = this.textField.getText();
                        this.textField.setText("");
                        this.searchButton.resetImage(
                                new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(
                                        "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Back.png"
                                )))
                        );
                        this.records2Copy = ClassAnalyzer.analyzeClass(
                                className,
                                HelloApplication.paths
                        );
                        this.records2 = ClassAnalyzer.analyzeClass(
                                className,
                                HelloApplication.paths
                        );
                        ClassAnalyzer.printClassInfo(this.records2);
                        updateTexts();
                    } else {
                        if (this.searchResultType.get(finalI).equals(SearchType.METHOD)) {
                            HelloApplication.openNodeEditor.addCard(
                                    NodeCreater.createNode(
                                            this.background.getX(),
                                            this.background.getY(),
                                            this.records2,
                                            this.records2.methods[finalI]
                                    ).createNew(
                                            this.background.getX(),
                                            this.background.getY(),
                                            HelloApplication.openNodeEditor.base
                                    )
                            );
                        } else {
                            HelloApplication.openNodeEditor.addCard(
                                    NodeCreater.createNode(
                                            this.background.getX(),
                                            this.background.getY(),
                                            this.records2,
                                            this.records2.fields[finalI]
                                    ).createNew(
                                            this.background.getX(),
                                            this.background.getY(),
                                            HelloApplication.openNodeEditor.base
                                    )
                            );
                        }
                        this.delete();
                        HelloApplication.cancelLabel = null;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            this.searchResultButtons.add(new Button[]{
                    new Button(startX + 5, startY + 40 + i * TEXT_HEIGHT - 15, startX + 25, startY + 40 + (i + 1) * TEXT_HEIGHT - 15, false),
                    new Button(startX + 25, startY + 40 + i * TEXT_HEIGHT - 15, startX + 45, startY + 40 + (i + 1) * TEXT_HEIGHT - 15, false),
                    new Button(startX + 45, startY + 40 + i * TEXT_HEIGHT - 15, startX + 65, startY + 40 + (i + 1) * TEXT_HEIGHT - 15, false),
                    new Button(startX + 65, startY + 40 + i * TEXT_HEIGHT - 15, startX + 85, startY + 40 + (i + 1) * TEXT_HEIGHT - 15, false)
            });
            this.searchResultType.add(SearchType.METHOD);
        }
        this.searchResultRecords = records;
        updateTexts();
        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!this.isFunction) {
                if (newValue.length() > oldValue.length()) {
                    this.searchResultRecords = this.searchResultRecords.stream()
                            .filter(item -> item.fullClassName().contains(newValue))
                            .collect(Collectors.toList());
                } else {
                    this.searchResultRecords = records;
                    this.searchResultRecords = this.searchResultRecords.stream()
                            .filter(item -> item.fullClassName().contains(newValue))
                            .collect(Collectors.toList());
                }
            } else {
                if (newValue.length() > oldValue.length()) {
                    this.records2.methods = Stream.of(this.records2.methods)
                            .filter(item -> item.name.contains(newValue))
                            .toList().toArray(new ClassAnalyzer.MethodInfo[0]);
                    this.records2.fields = Stream.of(this.records2.fields)
                            .filter(item -> item.name.contains(newValue))
                            .toArray(ClassAnalyzer.FieldInfo[]::new);
                    for (int i = 0; i < this.records2.methods.length; i++) {
                        if (i >= this.searchResultTexts.size()) break;
                        this.searchResultType.set(i, SearchType.METHOD);
                    }
                    for (int i = 0; i < this.records2.fields.length; i++) {
                        if (i >= this.searchResultTexts.size()) break;
                        this.searchResultType.set(i, SearchType.FIELD);
                    }
                } else {
                    this.records2.methods = this.records2Copy.methods.clone();
                    this.records2.fields = this.records2Copy.fields.clone();
                    this.records2.methods = Stream.of(this.records2.methods)
                            .filter(item -> item.name.contains(newValue))
                            .toList().toArray(new ClassAnalyzer.MethodInfo[0]);
                    this.records2.fields = Stream.of(this.records2.fields)
                            .filter(item -> item.name.contains(newValue))
                            .toArray(ClassAnalyzer.FieldInfo[]::new);
                    for (int i = 0; i < this.records2.methods.length; i++) {
                        if (i >= this.searchResultTexts.size()) break;
                        this.searchResultType.set(i, SearchType.METHOD);
                    }
                    for (int i = 0; i < this.records2.fields.length; i++) {
                        if (i >= this.searchResultTexts.size()) break;
                        this.searchResultType.set(i, SearchType.FIELD);
                    }
                }
            }

            updateTexts();

        });
    }

    public void updateTexts() {
        for (int i = 0; i < this.searchResultTexts.size(); i++) {
            if (this.searchResultRecords.size() - 1 < i) {
                this.searchResultTexts.get(i).setText("");
                this.searchResultButtons.get(i)[0].resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                        )))
                );
            } else {
                if (!this.isFunction) {
                    this.searchResultTexts.get(i).setText(this.searchResultRecords.get(i).fullClassName());
                } else {
                    this.searchResultTexts.get(i).setText("");
                }
                String pi = switch (this.searchResultRecords.get(i).type()) {
                    case TOP_LEVEL_CLASS -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/JavaClass.png";
                    case TOP_LEVEL_RECORD -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Record.png";
                    case TOP_LEVEL_ENUM -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Enum.png";
                    case TOP_LEVEL_INTERFACE -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Interface.png";
                    case TOP_LEVEL_ANNOTATION -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Annotation.png";
                    case TOP_LEVEL_ABSTRACT_CLASS ->
                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/AbstractClass.png";
                    case INNER_CLASS -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/NJavaClass.png";
                    case INNER_RECORD -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/NRecord.png";
                    case INNER_ENUM -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/NEnum.png";
                    case INNER_INTERFACE -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/NInterface.png";
                    case INNER_ANNOTATION -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/NAnnotation.png";
                    case INNER_ABSTRACT_CLASS ->
                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/NAbstractClass.png";
                };
                if (!isFunction) {
                    this.searchResultButtons.get(i)[0].resetImage(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                    pi
                            )))
                    );
                } else {
                    this.searchResultButtons.get(i)[0].resetImage(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                    "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                            )))
                    );
                }
            }
            this.searchResultButtons.get(i)[1].resetImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                    )))
            );
            this.searchResultButtons.get(i)[2].resetImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                    )))
            );
            this.searchResultButtons.get(i)[3].resetImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                    )))
            );
        }
        if (this.isFunction) {
            if (this.records2 == null) return;
            for (int i = 0; i < this.records2.methods.length; i++) {
                int r = 1;
                ClassAnalyzer.MethodInfo method = this.records2.methods[i];
                if (Modifier.isPublic(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Public.png"
                                    ))
                            )
                    );
                } else if (Modifier.isPrivate(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Private.png"
                                    ))
                            )
                    );
                } else if (Modifier.isProtected(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Protected.png"
                                    ))
                            )
                    );
                } else {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Default.png"
                                    ))
                            )
                    );
                }
                if (Modifier.isFinal(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Final.png"
                                    ))
                            )
                    );
                    r++;
                }
                if (Modifier.isStatic(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Static.png"
                                    ))
                            )
                    );
                    r++;
                }
                if (Modifier.isAbstract(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) this.searchResultButtons.get(i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Abstract.png"
                                    ))
                            )
                    );
                    r++;
                }
                if (Modifier.isSynchronized(method.modifiers)) {
                    if (i < this.searchResultButtons.size()) if (r < 4) this.searchResultButtons.get(i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Synchronized.png"
                                    ))
                            )
                    );
                }
                String params = String.join(", ", method.parameterTypes);
                String exceptions = method.exceptionTypes.length > 0 ?
                        " throws " + String.join(", ", method.exceptionTypes) : "";
                if (i < this.searchResultButtons.size()) this.searchResultTexts.get(i).setText(
                        method.returnType + " " +
                                method.name + "(" + params + ")" + exceptions);
            }
            for (int i = 0; i < this.records2.fields.length; i++) {
                int r = 1;
                ClassAnalyzer.FieldInfo method = this.records2.fields[i];
                if (Modifier.isPublic(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Public.png"
                                    ))
                            )
                    );
                } else if (Modifier.isPrivate(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Private.png"
                                    ))
                            )
                    );
                } else if (Modifier.isProtected(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Protected.png"
                                    ))
                            )
                    );
                } else {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[0].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Default.png"
                                    ))
                            )
                    );
                }
                if (Modifier.isFinal(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Final.png"
                                    ))
                            )
                    );
                    r++;
                }
                if (Modifier.isStatic(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Static.png"
                                    ))
                            )
                    );
                    r++;
                }
                if (Modifier.isAbstract(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultButtons.get(this.records2.methods.length + i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Abstract.png"
                                    ))
                            )
                    );
                    r++;
                }
                if (Modifier.isSynchronized(method.modifiers)) {
                    if (this.records2.methods.length + i < this.searchResultButtons.size()) if (r < 4) this.searchResultButtons.get(this.records2.methods.length + i)[r].resetImage(
                            new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Synchronized.png"
                                    ))
                            )
                    );
                }
                String params = Modifier.toString(method.modifiers) + " " +
                        method.type + " " +
                        method.name;
                if (this.records2.methods.length + i < this.searchResultButtons.size()) this.searchResultTexts.get(this.records2.methods.length + i).setText(
                        params
                );
            }

        }
    }

    @Override
    public void resetPos(double x, double y) {

    }

    @Override
    public void resetSize(double width, double height) {

    }

    @Override
    public void delete() {
        this.searchButton.delete();
        for (Button[] searchResultButton : this.searchResultButtons) {
            searchResultButton[0].delete();
            searchResultButton[1].delete();
            searchResultButton[2].delete();
            searchResultButton[3].delete();
        }
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.textField);
        this.searchButton.addTo(this.base);
        for (Text searchResultText : this.searchResultTexts) {
            this.base.getChildren().add(searchResultText);
        }
        for (Button[] searchResultButton : this.searchResultButtons) {
            searchResultButton[0].addTo(this.base);
            searchResultButton[1].addTo(this.base);
            searchResultButton[2].addTo(this.base);
            searchResultButton[3].addTo(this.base);
        }
        this.base.setClip(getMask());
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public BaseLabel createNew() {
        return null;
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }

    public enum SearchType {
        METHOD,
        FIELD
    }

    public Rectangle getMask() {
        return new Rectangle(this.background.getX(), this.background.getY(), this.background.getWidth(), this.background.getHeight());
    }

}
