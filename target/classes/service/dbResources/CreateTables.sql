drop schema project;
create schema project;

CREATE TABLE `project`.`bank`(
    `id` INT NOT NULL,
    `name` VARCHAR(45) NOT NULL,
    `location` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`));

CREATE TABLE `project`.`client`(
    `firstName` VARCHAR(45) NOT NULL,
    `lastName` VARCHAR(45) NOT NULL,
    `age` INT NOT NULL,
    `cnp` VARCHAR(45) NOT NULL UNIQUE,
    `bankID` INT,
    PRIMARY KEY (`cnp`),
    FOREIGN KEY (`bankID`) REFERENCES project.bank(`id`) ON DELETE CASCADE);

CREATE TABLE `project`.`bankAccount`(
    `id` INT NOT NULL,
    `IBAN` VARCHAR(45) NOT NULL UNIQUE,
    `cnp` VARCHAR(45),
    `openingDate` VARCHAR(45) NOT NULL,
    `closingDate` VARCHAR(45),
    `balance` DOUBLE,
    `currency` VARCHAR(45),
    `annualInterestRate` DOUBLE,
    PRIMARY KEY (`IBAN`),
    FOREIGN KEY (`cnp`) REFERENCES project.client(`cnp`) ON DELETE CASCADE);

CREATE TABLE `project`.`card`(
    `cardNumber` VARCHAR(45) NOT NULL UNIQUE,
    `IBAN` VARCHAR(45),
    `PIN` INT NOT NULL,
    `issueDate` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`cardNumber`),
    FOREIGN KEY (`IBAN`) REFERENCES project.bankaccount(`IBAN`) ON DELETE CASCADE);

CREATE TABLE `project`.`loan`(
    `id` INT NOT NULL,
    `cnp` VARCHAR(45),
    `value` DOUBLE NOT NULL,
    `currency` VARCHAR(45) NOT NULL,
    `detail` VARCHAR(45) NOT NULL,
    `date` VARCHAR(45) NOT NULL,
    `durationMonths` INT NOT NULL,
    PRIMARY KEY (`cnp`,`date`),
    FOREIGN KEY (`cnp`) REFERENCES project.client(`cnp`) ON DELETE CASCADE);

CREATE TABLE `project`.`provider`(
    `company` VARCHAR(45) NOT NULL,
    `IBAN` VARCHAR(45) NOT NULL UNIQUE,
    `balance` DOUBLE,
    `currency` VARCHAR(45),
    PRIMARY KEY (`IBAN`));

CREATE TABLE `project`.`transaction`(
    `transactionID` VARCHAR(120) NOT NULL UNIQUE,
    `IBAN` VARCHAR(45) NOT NULL,
    `timestamp` VARCHAR(45) NOT NULL,
    `tradeValue` DOUBLE NOT NULL,
    `currency` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`transactionID`),
    FOREIGN KEY (`IBAN`) REFERENCES project.bankaccount(`IBAN`) ON DELETE CASCADE);