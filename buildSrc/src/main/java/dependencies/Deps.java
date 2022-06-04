package dependencies;

public class Deps {
    public static final String allureVersion = "2.15.0";
    public static final String qametaAllure = "io.qameta.allure";
    public static final String allureJavaCommons = qametaAllure + ":allure-java-commons:" + allureVersion;
    public static final String allureJunit = qametaAllure + ":allure-junit5:" + allureVersion;
    public static final String allureAssertJ = qametaAllure + ":allure-assertj:" + allureVersion;
    public static final String allureCommandLine = qametaAllure + ":allure-commandline:" + allureVersion;
    public static final String allureSelenide = qametaAllure + ":allure-selenide:" + allureVersion;

    public static final String jacksonVersion = "2.13.2";
    public static final String jackson = "com.fasterxml.jackson.core";
    public static final String jacksonCore = jackson + ":jackson-core:" + jacksonVersion;
    public static final String jacksonDataBind = jackson + ":jackson-databind:" + jacksonVersion;

    public static final String junitVersion = "5.8.1";
    public static final String junit = "org.junit.jupiter";
    public static final String junitApi = junit + ":junit-jupiter-api:" + junitVersion;
    public static final String junitEngine = junit + ":junit-jupiter-engine:" + junitVersion;

    public static final String aspectJVersion = "1.9.8.M1";
    public static final String aspectJ = "org.aspectj";
    public static final String aspectJWeaver = aspectJ + ":aspectjweaver:" + aspectJVersion;

    public static final String postgresqlVersion = "42.3.5";
    public static final String postgresqlOrg = "org.postgresql";
    public static final String postgresql = postgresqlOrg + ":postgresql:" + postgresqlVersion;

    public static final String selenideVersion = "6.3.5";
    public static final String codeborne = "com.codeborne";
    public static final String selenide = codeborne + ":selenide:" + selenideVersion;

    public static final String jooqVersion = "3.16.6";
    public static final String orgJooq = "org.jooq";
    public static final String jooqDep = orgJooq + ":jooq:" + jooqVersion;

    public static final String javaFakerVersion = "1.0.2";
    public static final String githubJavaFaker= "com.github.javafaker";
    public static final String javaFaker = githubJavaFaker+":javafaker:"+javaFakerVersion;
}
