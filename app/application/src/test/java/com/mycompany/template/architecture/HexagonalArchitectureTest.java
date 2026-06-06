package com.mycompany.template.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.inject.Named;
import jakarta.persistence.Entity;
import org.mapstruct.Mapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.mycompany.template",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTest {

    // ────────────────────────────────────────────────────────────
    // Grupo 1 — Core livre de frameworks
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule core_deve_ser_livre_de_spring =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule core_deve_ser_livre_de_jpa =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");

    @ArchTest
    static final ArchRule core_deve_ser_livre_de_jackson =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAPackage("com.fasterxml.jackson..");

    @ArchTest
    static final ArchRule core_nao_deve_depender_de_infra =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAPackage("..infra..");

    // ────────────────────────────────────────────────────────────
    // Grupo 2 — UseCase conventions
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule usecases_devem_ter_anotacao_named =
            classes().that().resideInAPackage("..core.usecase..")
                    .should().beAnnotatedWith(Named.class);

    @ArchTest
    static final ArchRule named_somente_em_core_usecase =
            classes().that().areAnnotatedWith(Named.class)
                    .should().resideInAPackage("..core.usecase..");

    @ArchTest
    static final ArchRule usecases_devem_ter_sufixo_usecaseimpl =
            classes().that().resideInAPackage("..core.usecase..")
                    .should().haveSimpleNameEndingWith("UseCaseImpl");

    // ────────────────────────────────────────────────────────────
    // Grupo 3 — Ports são interfaces
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule ports_in_devem_ser_interfaces =
            classes().that().resideInAPackage("..core.ports.in..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule ports_out_devem_ser_interfaces =
            classes().that().resideInAPackage("..core.ports.out..")
                    .should().beInterfaces();

    // ────────────────────────────────────────────────────────────
    // Grupo 4 — @RestController localização
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule rest_controller_somente_em_api_controller =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..infra.api.controller..");

    // ────────────────────────────────────────────────────────────
    // Grupo 5 — @Entity localização
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule entity_somente_em_infra_entity =
            classes().that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..infra..entity..");

    // ────────────────────────────────────────────────────────────
    // Grupo 6 — Listeners de mensageria no pacote correto
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule kafka_listener_somente_em_infra_kafka_listener =
            methods().that().areAnnotatedWith(KafkaListener.class)
                    .should(beDeclaredInPackageContaining("infra.kafka.listener"));

    @ArchTest
    static final ArchRule sqs_listener_somente_em_infra_sqs_listener =
            methods().that().areAnnotatedWith(SqsListener.class)
                    .should(beDeclaredInPackageContaining("infra.sqs.listener"));

    // ────────────────────────────────────────────────────────────
    // Grupo 7 — Mappers em pacote correto
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule mappers_somente_em_pacote_mapper =
            classes().that().areAnnotatedWith(Mapper.class)
                    .should().resideInAPackage("..mapper..");

    // ────────────────────────────────────────────────────────────
    // Grupo 8 — Inbound adapters sem interfaces manuais
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule controllers_nao_implementam_interfaces =
            classes().that().resideInAPackage("..infra.api.controller..")
                    .should(notImplementUserDefinedInterfaces());

    @ArchTest
    static final ArchRule kafka_listeners_nao_implementam_interfaces =
            classes().that().resideInAPackage("..infra.kafka.listener..")
                    .should(notImplementUserDefinedInterfaces());

    @ArchTest
    static final ArchRule sqs_listeners_nao_implementam_interfaces =
            classes().that().resideInAPackage("..infra.sqs.listener..")
                    .should(notImplementUserDefinedInterfaces());

    // ────────────────────────────────────────────────────────────
    // Grupo 9 — Adapters implementam Port do core
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule adapters_devem_implementar_port_do_core =
            classes().that().haveSimpleNameEndingWith("Adapter")
                    .should(implementPortFromCore());

    // ────────────────────────────────────────────────────────────
    // Grupo 10 — Sem ciclos entre pacotes de primeiro nível
    // ────────────────────────────────────────────────────────────

    @ArchTest
    static final ArchRule sem_ciclos_entre_pacotes =
            SlicesRuleDefinition.slices()
                    .matching("com.mycompany.template.(*)..")
                    .should().beFreeOfCycles();

    // ────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────

    private static ArchCondition<JavaMethod> beDeclaredInPackageContaining(String fragment) {
        return new ArchCondition<>("be declared in a class whose package contains '" + fragment + "'") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                if (!method.getOwner().getPackageName().contains(fragment)) {
                    events.add(SimpleConditionEvent.violated(method,
                            method.getFullName() + " is declared in package '"
                                    + method.getOwner().getPackageName() + "'"));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> notImplementUserDefinedInterfaces() {
        return new ArchCondition<>("not implement user-defined interfaces") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                item.getInterfaces().stream()
                        .map(JavaType::toErasure)
                        .filter(iface -> !iface.getPackageName().startsWith("java.")
                                && !iface.getPackageName().startsWith("jakarta.")
                                && !iface.getPackageName().startsWith("org.springframework."))
                        .forEach(iface -> events.add(SimpleConditionEvent.violated(item,
                                item.getName() + " implements user-defined interface " + iface.getName())));
            }
        };
    }

    private static ArchCondition<JavaClass> implementPortFromCore() {
        return new ArchCondition<>("implement at least one interface from ..core.ports.out..") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean implementsPort = item.getInterfaces().stream()
                        .map(JavaType::toErasure)
                        .anyMatch(iface -> iface.getPackageName().contains("core.ports.out"));
                if (!implementsPort) {
                    events.add(SimpleConditionEvent.violated(item,
                            item.getName() + " does not implement any interface from core.ports.out"));
                }
            }
        };
    }
}
