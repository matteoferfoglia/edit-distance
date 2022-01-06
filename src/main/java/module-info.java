/**
 * Edit distance package.
 */
module edit_distance {
    requires org.jetbrains.annotations;
    requires org.apache.commons.lang3;
    requires benchmark;

    opens edit_distance;
    opens edit_distance.utils;
    opens edit_distance.entities;

    exports edit_distance;
    exports edit_distance.utils;
    exports edit_distance.entities;
}