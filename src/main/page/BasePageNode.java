package main.page;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.utils.ResourceUtils;
import main.utils.StyleUtils;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public abstract class BasePageNode extends VBox {
    protected Pane mNode;

    protected Pane mRoot;

    public BasePageNode() {
        if (mRoot == null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setController(this);
            fxmlLoader.setResources(ResourceBundle.getBundle("language"));
            fxmlLoader.setLocation(ResourceUtils.getXmlUrl(getFxmLPath()));
            try {
                mRoot = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            StyleUtils.addStyleNode(mRoot);
            mRoot = (Pane) mRoot.lookup("#root");
            mRoot.setPadding(new Insets(30));
            initView();
            initData();
            initRasterizeWidth(getRasterizeWidthPanes());
        }
    }

    public Pane getNode() {
        return mRoot;
    }

    abstract String getFxmLPath();

    abstract void initView();

    abstract void initData();
    abstract List<Pane> getRasterizeWidthPanes();

    abstract List<Pane> getRasterizeHeightPanes();
    void initRasterizeWidth(List<Pane> panes) {
        if (panes == null) {
            return;
        }
        for (Pane pane : panes) {
            if (pane == null) {
                continue;
            }
            Pane root = mRoot;
            root.setPrefWidth(1000);
//            if (root != null) {
//                pane.prefWidthProperty().bind(root.widthProperty());
//            }
        }
    }

    void initRasterizeHeight(List<Pane> panes) {
        if (panes == null) {
            return;
        }
        for (Pane pane : panes) {
            if (pane == null) {
                continue;
            }
//            Pane root = PageRouter.getPageRoot();
//            if (root != null) {
//                pane.prefHeightProperty().bind(root.heightProperty());
//            }
        }
    }

    public abstract void refresh();
}
