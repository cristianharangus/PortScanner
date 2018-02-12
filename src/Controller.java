import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Controller {

    private boolean InsufficientDataForScan;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblTarget;

    @FXML
    private Label lblPortStart;

    @FXML
    private Label lblPortEnd;

    @FXML
    private TextField txtFieldTarget;

    @FXML
    private TextField txtFieldPortStart;

    @FXML
    private TextField txtFieldPortEnd;

    @FXML
    private ScrollPane sp;

    @FXML
    private TextFlow txtFlowMessage;

    @FXML
    private Button btnScan;

    @FXML
    private Button btnReset;

    @FXML
    private Label lblStatus;

    @FXML
    void initialize() {
        txtFlowMessage.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    txtFlowMessage.layout();
                    sp.layout();
                    sp.setVvalue(1.0f);
                }));
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setContent(txtFlowMessage);
    }

    public TextField getTxtFieldTarget() {
        return txtFieldTarget;
    }

    public TextField getTxtFieldPortStart() {
        return txtFieldPortStart;
    }

    public TextField getTxtFieldPortEnd() {
        return txtFieldPortEnd;
    }

    public TextFlow getTxtFlowMessage() {
        return txtFlowMessage;
    }

    public Button getBtnReset() {
        return btnReset;
    }

    public Label getLblStatus() {
        return lblStatus;
    }


    public void setInsufficientDataForScan(boolean insufficientDataForScan) {
        InsufficientDataForScan = insufficientDataForScan;
    }

    public void Reset(MouseEvent mouseEvent) {
        this.getTxtFieldTarget().setText("");
        this.getTxtFieldPortStart().setText("");
        this.getTxtFieldPortEnd().setText("");
        this.getTxtFlowMessage().getChildren().clear();
        this.getBtnReset().setText("Reset");
    }

    public void Scan(MouseEvent mouseEvent) {
        boolean ok = true;

        this.getTxtFlowMessage().getChildren().clear();

        if(this.getTxtFieldTarget().getText().equals(""))
        {
            this.setLog("error", "Enter the IP or address of scan target.");
            ok = false;
        }
        if (this.getTxtFieldPortStart().getText().equals(""))
        {
            this.setLog("error", "Enter the start port for scan.");
            ok = false;
        }

        if (this.getTxtFieldPortEnd().getText().equals(""))
        {
            this.setLog("error", "Enter the end port for scan.");
            ok = false;
        }

        if(!ok) { return; }

        this.setInsufficientDataForScan(true);

//      --------------------------------------------------------------------------------------------------------------

        String host = this.getTxtFieldTarget().getText();
        int portStart = Integer.parseInt(this.getTxtFieldPortStart().getText());
        int portEnd = Integer.parseInt(this.getTxtFieldPortEnd().getText());
        if (portStart >= portEnd)
        {
            this.setLog("error", "Beginning port should be smaller than the ending one");
            return;
        }

        this.setLog("info", "Started scanning .....");

        ExecutorService executor= new ThreadPoolExecutor(20, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        for (int port = portStart; port <= portEnd; port++)
            executor.submit(new PortChecker(host, port, this));
        executor.shutdown();

//      --------------------------------------------------------------------------------------------------------------
    }

    public void setLog(String type, String log) {
        Platform.runLater(() -> {
            Text text = new Text();
            switch (type) {
                case "error":
                    text.setStyle("-fx-fill: #bb0000;-fx-font-weight:bold;");
                    break;
                case "success":
                    text.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
                    break;
                case "warning":
                    text.setStyle("-fx-fill: #9F6000;-fx-font-weight:bold;");
                    break;
                case "info":
                    text.setStyle("-fx-fill: #488ed4;-fx-font-weight:bold;");
                    break;
                default:
                    text.setStyle("-fx-fill: #000;-fx-font-weight:normal;");
                    break;
            }
            text.setText(">> " + log + "\n");
            this.getTxtFlowMessage().getChildren().add(text);
        });
    }

    public void setStatus(String str)
    {
        Platform.runLater(() -> {
            this.getLblStatus().setText("Now scanning port: " + str);
            if (Integer.parseInt(str) == Integer.parseInt(getTxtFieldPortEnd().getText())) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.setLog("info", "End of scan.....");

            }
        });
    }
}
