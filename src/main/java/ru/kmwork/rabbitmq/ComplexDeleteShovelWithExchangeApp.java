package ru.kmwork.rabbitmq;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComplexDeleteShovelWithExchangeApp {
  public static void main(String[] args) {
    log.info("[ComplexDeleteShovelWithExchangeApp] **************** start ****************");
    DeleteShovelApp.main(args);
    DropExchangeApp.main(args);
    log.info("[ComplexDeleteShovelWithExchangeApp] **************** end ****************");
  }
}
