package net.blancworks.figura.lua.api.model;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.blancworks.figura.lua.CustomScript;
import net.blancworks.figura.lua.LuaUtils;
import net.blancworks.figura.lua.api.ReadOnlyLuaTable;
import net.blancworks.figura.lua.api.ScriptLocalAPITable;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ElytraModelAPI {

    public static final String VANILLA_LEFT_WING = "LEFT_WING";
    public static final String VANILLA_RIGHT_WING = "RIGHT_WING";

    public static final Identifier VANILLA_LEFT_WING_ID = new Identifier("figura", "left_wing");
    public static final Identifier VANILLA_RIGHT_WING_ID = new Identifier("figura", "right_wing");
    
    public static Identifier getID() {
        return new Identifier("default", "elytra_model");
    }

    public static ReadOnlyLuaTable getForScript(CustomScript script) {
        ScriptLocalAPITable producedTable = new ScriptLocalAPITable(script, new LuaTable() {{
            PlayerEntityModel mdl = script.playerData.vanillaModel;

            set(VANILLA_LEFT_WING, getTableForPart(VANILLA_LEFT_WING, script));
            set(VANILLA_RIGHT_WING, getTableForPart(VANILLA_RIGHT_WING, script));
        }});

        return producedTable;
    }

    public static ReadOnlyLuaTable getTableForPart(String accessor, CustomScript script) {
        ElytraPartTable producedTable = new ElytraPartTable(accessor, script);
        return producedTable;
    }

    private static class ElytraPartTable extends ScriptLocalAPITable {
        String accessor;

        public ElytraPartTable(String accessor, CustomScript script) {
            super(script);
            this.accessor = accessor;
            super.setTable(getTable());
        }

        public LuaTable getTable() {
            LuaTable ret = new LuaTable();

            ret.set("getPos", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    Vector3f v = targetScript.getOrMakePartCustomization(accessor).pos;

                    if(v == null)
                        return NIL;

                    return LuaUtils.getTableFromVector3f(v);
                }
            });
            
            ret.set("setPos", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1) {
                    FloatArrayList fas = LuaUtils.getFloatsFromTable(arg1.checktable());
                    VanillaModelPartCustomization customization = targetScript.getOrMakePartCustomization(accessor);
                    customization.pos = new Vector3f(
                            fas.getFloat(0),
                            fas.getFloat(1),
                            fas.getFloat(2)
                    );

                    return NIL;
                }
            });

            ret.set("getRot", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    Vector3f v = targetScript.getOrMakePartCustomization(accessor).rot;

                    if (v == null)
                        return NIL;

                    return LuaUtils.getTableFromVector3f(v);
                }
            });

            ret.set("setRot", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1) {
                    FloatArrayList fas = LuaUtils.getFloatsFromTable(arg1.checktable());
                    VanillaModelPartCustomization customization = targetScript.getOrMakePartCustomization(accessor);
                    customization.rot = new Vector3f(
                            fas.getFloat(0),
                            fas.getFloat(1),
                            fas.getFloat(2)
                    );
                    return NIL;
                }
            });

            ret.set("getEnabled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    VanillaModelPartCustomization customization = targetScript.getOrMakePartCustomization(accessor);

                    if (customization != null)
                        return LuaBoolean.valueOf(customization.visible);

                    return NIL;
                }
            });

            ret.set("setEnabled", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    VanillaModelPartCustomization customization = targetScript.getOrMakePartCustomization(accessor);

                    if (arg.isnil()) {
                        customization.visible = null;
                        return NIL;
                    }

                    customization.visible = arg.checkboolean();

                    return NIL;
                }
            });

            return ret;
        }
    }

}
