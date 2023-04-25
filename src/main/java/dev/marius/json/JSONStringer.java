/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package dev.marius.json;
/*

Project: Lobby-System
Author: Marius
Created: 17.10.2020, 12:48

*/

import java.io.StringWriter;

public class JSONStringer extends JSONWriter {

    public JSONStringer() {
        super(new StringWriter());
    }

    public String toString() {
        return this.mode == 'd' ? this.writer.toString() : null;
    }

}
