/**
 * Edit distance package.
 */
module main_package {
    requires org.jetbrains.annotations;
    requires org.apache.commons.lang3;
    requires benchmark;

    opens main_package;
    opens main_package.utils;
    opens main_package.entities;
    opens main_package.deprecated;

    exports main_package;
    exports main_package.utils;
    exports main_package.entities;
    exports main_package.deprecated;
}