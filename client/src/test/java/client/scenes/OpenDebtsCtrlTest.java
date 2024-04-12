package client.scenes;

import client.utils.ServerUtils;
import commons.dto.DebtDTO;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class OpenDebtsCtrlTest extends ApplicationTest {

    private static EventDTO eventMock;
    private static MainCtrl mainCtrlMock;
    private static ServerUtils serverUtilsMock;
    private static OpenDebtsCtrl openDebtsCtrl;
    private static DebtDTO debtDTOMock;
    private static ParticipantDTO debtorMock;
    private static ParticipantDTO creditorMock;
    private static VBox debtVBox;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(mainCtrlMock);
    }

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");


        eventMock = mock(EventDTO.class);
        mainCtrlMock = mock(MainCtrl.class);
        serverUtilsMock = mock(ServerUtils.class);
        openDebtsCtrl = new OpenDebtsCtrl(mainCtrlMock, serverUtilsMock);
        debtDTOMock = mock(DebtDTO.class);
        debtorMock = mock(ParticipantDTO.class);
        creditorMock = mock(ParticipantDTO.class);
        when(debtDTOMock.debtorName()).thenReturn("John");
        when(debtDTOMock.creditorName()).thenReturn("Jane");
        when(debtDTOMock.amount()).thenReturn(100.0);
        when(eventMock.code()).thenReturn("code");
        when(serverUtilsMock.getParticipant(eventMock.code(), "John")).thenReturn(debtorMock);
        when(serverUtilsMock.getParticipant(eventMock.code(), "Jane")).thenReturn(creditorMock);
        when(debtorMock.name()).thenReturn("John");
        when(creditorMock.name()).thenReturn("Jane");
        when(creditorMock.iban()).thenReturn("IBAN");
        when(creditorMock.bic()).thenReturn("BIC");

        debtVBox = new VBox();
        openDebtsCtrl.setDebtVBox(debtVBox);
        openDebtsCtrl.refresh(eventMock);
    }


    @Test
    public void testGoBack() {
        openDebtsCtrl.goBack();
        verify(mainCtrlMock, times(1)).showEventOverview(eventMock);
        // Check that showEventOverview was called with eventMock
    }

    @Test
    public void testOnGlobalKeyPress() {
        KeyEvent keyEventMock = mock(KeyEvent.class);

        // Case 1: KeyCode is ESCAPE
        when(keyEventMock.getCode()).thenReturn(KeyCode.ESCAPE);
        openDebtsCtrl.onGlobalKeyPress(keyEventMock);
        verify(mainCtrlMock, times(1)).showEventOverview(eventMock);
        // Check that showEventOverview was called with eventMock

        // Case 2: KeyCode is not ESCAPE
        when(keyEventMock.getCode()).thenReturn(KeyCode.A);
        openDebtsCtrl.onGlobalKeyPress(keyEventMock);
        verify(mainCtrlMock, times(1)).showEventOverview(eventMock);
        // Check that showEventOverview was not called again
    }

//    @Test
//    public void testAddDebtToLayout() {
//        openDebtsCtrl.addDebtToLayout(debtDTOMock);
//
//        assertFalse(debtVBox.getChildren().isEmpty()); // Check that the VBox is not empty
//        VBox debtItem = (VBox) debtVBox.getChildren().get(0);
//        HBox debtLine = (HBox) debtItem.getChildren().get(0);
//        Button receivedButton = (Button) debtLine.getChildren().get(2);
//
//        when(debtDTOMock.received()).thenReturn(false);
//
//        receivedButton.fire(); // Simulate a button click
//
//        assertEquals("Undo", receivedButton.getText()); // Check the text of the Button when debt is received
//
//        when(debtDTOMock.received()).thenReturn(true);
//
//        receivedButton.fire(); // Simulate another button click
//
//        assertEquals("Mark received", receivedButton.getText());
//        // Check the text of the Button when debt is not received
//
//        ToggleButton moreInfoButton = (ToggleButton) debtLine.getChildren().get(0);
//
//        assertFalse(moreInfoButton.isSelected()); // Check that the button is not selected initially
//
//        moreInfoButton.fire(); // Simulate a button click
//
//        assertTrue(moreInfoButton.isSelected()); // Check that the button is selected after click
//        assertEquals("v", moreInfoButton.getText()); // Check the text of the Button when selected
//
//        moreInfoButton.fire(); // Simulate another button click
//
//        assertFalse(moreInfoButton.isSelected()); // Check that the button is not selected after click
//        assertEquals(">", moreInfoButton.getText()); // Check the text of the Button when not selected
//    }

    /**
     * This test is dropped since the introduction of the translation feature
     */
    @Test
    public void testGetBankInfoText() {
//        String result = openDebtsCtrl.getBankInfoText(debtDTOMock);
//
//        String expected = "Debt Details:\n" +
//                "Debtor: John\n" +
//                "Creditor: Jane\n" +
//                "Amount: 100.0 Euro\n\n" +
//                "Bank Information for creditor (Jane):\n" +
//                "Account Holder: Jane\n" +
//                "IBAN: IBAN\n" +
//                "BIC: BIC";
//        assertEquals(expected, result);
    }
}
