package ru.netris.itx2.libraries.rabbitmq;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComplexRegistrationShovelWithExchangeApp {

  private static final int MESSAGE_COUNT = 10;
  private static final String JSON_FILE_NAME = "json_welcome.json";


  @SneakyThrows
  public static void main(String[] args) {
    log.info("[ComplexRegistrationShovelWithExchangeApp] **************** start ****************");

    /* ------DEL------- */
    log.info("[ComplexRegistrationShovelWithExchangeApp] start: delete old info");
    DeleteShovelApp.main(args);
    DropExchangeApp.main(args);
    MqDropVHostApp.main(args);
    log.info("[ComplexRegistrationShovelWithExchangeApp] end: delete old info");

    /* ------REG------- */
    log.info("[ComplexRegistrationShovelWithExchangeApp] start: new registration");
    MqRegVHostApp.main(args);
    MqRegExchangeApp.main(args);
    MqRegShovelBridgeApp.main(args);
    log.info("[ComplexRegistrationShovelWithExchangeApp] end: new registration");
    /* -----END:REG----- */

    /* ------TESING------- */
    MqMessageSendTester.publishMessagesInBatch(MESSAGE_COUNT, JSON_FILE_NAME);
    /* ------END: TESING------- */
    log.info("[ComplexRegistrationShovelWithExchangeApp] **************** end ****************");
  }
}
