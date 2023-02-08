package ac.artemis.core.v4.utils.chat;

import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Ghast
 * @since 10/11/2020
 * HideMySkewnessCheckObfuscator © 2020
 */
public class FuckYouSkid {

    private static Unsafe unsafe;
    private static Method nativeMethod;
    private static boolean injected;

    public enum Pointers {

        // Strucs
        STRUCT_DEFAULT("gHotSpotVMStructs"),
        STRUCT_ARRAY("gHotSpotVMStructEntryArrayStride"),
        STRUCT_TYPE("gHotSpotVMStructEntryTypeNameOffset"),
        STRUCT_FIELD("gHotSpotVMStructEntryFieldNameOffset"),
        STRUCT_TYPE_STRING("gHotSpotVMStructEntryTypeStringOffset"),
        STRUCT_STATIC("gHotSpotVMStructEntryIsStaticOffset"),
        STRUCT_ADDRESS("gHotSpotVMStructEntryAddressOffset"),
        STRUCT_OFFSET("gHotSpotVMStructEntryOffsetOffset"),
        // Types
        TYPE_DEFAULT("gHotSpotVMTypes"),
        TYPE_ARRAY("gHotSpotVMTypeEntryArrayStride"),
        TYPE_NAME("gHotSpotVMTypeEntryTypeNameOffset"),
        TYPE_SUPERCLASS("gHotSpotVMTypeEntrySuperclassNameOffset"),
        TYPE_SIZE("gHotSpotVMTypeEntrySizeOffset"),
        TYPE_OOP("gHotSpotVMTypeEntryIsOopTypeOffset"),
        TYPE_INT("gHotSpotVMTypeEntryIsIntegerTypeOffset"),
        TYPE_UNSIGNED("gHotSpotVMTypeEntryIsUnsignedOffset"),

        ;



        private final String name;

        public String getName() {
            return name;
        }

        Pointers(String name) {
            this.name = name;
        }
    }

    public static class JVMHandler {
        private final String name;
        private Map<String, JVMField> fieldMap = new WeakHashMap<>();

        public static class JVMField {
            private final String name, type;
            private final long offset;
            private final boolean statiz;

            public JVMField(String name, String type, long offset, boolean statiz) {
                this.name = name;
                this.type = type;
                this.offset = offset;
                this.statiz = statiz;
            }

            public long getOffset() {
                return offset;
            }
        }

        public JVMHandler(String name) {
            this.name = name;
        }

        public Map<String, JVMField> getFieldMap() {
            return fieldMap;
        }
    }

    public static class JVMType {
        private final String type, superclass;
        private final int size;
        private final boolean oop, integer, unsigned;
        private final Map<String, JVMHandler.JVMField> fieldMap = new WeakHashMap<>();

        public JVMType(String type, String superclass, int size, boolean oop, boolean integer, boolean unsigned,
                       Map<String, JVMHandler.JVMField> fieldMap) {
            this.type = type;
            this.superclass = superclass;
            this.size = size;
            this.oop = oop;
            this.integer = integer;
            this.unsigned = unsigned;

            if (fieldMap != null) {
                this.fieldMap.putAll(fieldMap);
            }
        }

        public String getType() {
            return type;
        }

        public int getSize() {
            return size;
        }

        public boolean isOop() {
            return oop;
        }

        public Map<String, JVMHandler.JVMField> getFieldMap() {
            return fieldMap;
        }
    }

    public static class JVMFlag {
        private final String name;
        private final long address;

        public JVMFlag(String name, long address) {
            this.name = name;
            this.address = address;
        }
    }

    private static long findNative(String name, ClassLoader classLoader)
            throws InvocationTargetException, IllegalAccessException {
        return (long) nativeMethod.invoke(null, classLoader, name);
    }

    private static native long findNative(String name);

    private static long findPointer(Pointers pointer) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("windows")) {
                String virtual = System.getProperty("java.vm.name").contains("Client VM")
                        ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
                System.load(System.getProperty("java.home") + virtual);
            } else if (os.contains("linux")) {
                String virtual = System.getProperty("java.vm.name").contains("Client VM")
                        ? "/lib/amd64/client/libjvm.so" : "/lib/amd64/server/libjvm.so";
                System.load(System.getProperty("java.home") + virtual);
            } else {
                throw new RuntimeException("You are running on an unsupported JVM");
            }

            long nativ = findNative(pointer.getName(), ClassLoader.getSystemClassLoader());
            if (nativ == 0L) throw new RuntimeException("Failed to find pointer of type " + pointer + "!");
            return unsafe.getLong(nativ);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private static String parseOffset(long addr) {
        if (addr == 0L) return null;

        StringBuilder builder = new StringBuilder();
        int offset = 0;

        while (true) {
            char ch = (char) unsafe.getByte(addr + offset++);
            if (ch == '\u0000') break;
            builder.append(ch);
        }

        return builder.toString();
    }

    private static String readStringFromPointer(long addr) {
        return parseOffset(unsafe.getLong(addr));
    }

    private Map<String, JVMType> getTypes(Map<String, JVMHandler> structs) {

        long entry = findPointer(Pointers.TYPE_DEFAULT);
        long array = findPointer(Pointers.TYPE_ARRAY);

        Map<String, JVMType> types = new WeakHashMap<>();

        while (true) {
            String typeName = readStringFromPointer(entry + findPointer(Pointers.TYPE_NAME));
            if (typeName == null) break;

            String superClassName = readStringFromPointer(entry + findPointer(Pointers.TYPE_SUPERCLASS));

            final int size = unsafe.getInt(entry + findPointer(Pointers.TYPE_SIZE));
            final boolean in = unsafe.getInt(entry + findPointer(Pointers.TYPE_INT)) != 0;
            final boolean oop = unsafe.getInt(entry + findPointer(Pointers.TYPE_OOP)) != 0;
            final boolean unsigned = unsafe.getInt(entry + findPointer(Pointers.TYPE_UNSIGNED)) != 0;

            JVMHandler handler = structs.get(typeName);
            Map<String, JVMHandler.JVMField> fields = null;

            if (handler != null) fields = handler.getFieldMap();
            types.put(typeName, new JVMType(typeName, superClassName, size, oop, in, unsigned, fields));
            entry += array;
        }

        return types;
    }

    private Map<String, JVMHandler> getStructs() {
        Map<String, JVMHandler> structs = new WeakHashMap<>();

        long entry = findPointer(Pointers.STRUCT_DEFAULT);
        final long array = findPointer(Pointers.STRUCT_ARRAY);

        while (true) {
            final String typeName = readStringFromPointer(entry + findPointer(Pointers.STRUCT_TYPE));
            final String fieldName = readStringFromPointer(entry + findPointer(Pointers.STRUCT_FIELD));

            if (typeName == null || fieldName == null) break;

            final String typeString = readStringFromPointer(entry + findPointer(Pointers.STRUCT_TYPE_STRING));
            final boolean statiz = unsafe.getInt(entry + findPointer(Pointers.STRUCT_STATIC)) != 0;

            final long offsetOffset = statiz ? findPointer(Pointers.STRUCT_ADDRESS) : findPointer(Pointers.STRUCT_OFFSET);
            final long offset = unsafe.getLong(entry + offsetOffset);

            JVMHandler handler = structs.get(typeName);
            if (handler == null) {
                handler = new JVMHandler(typeName);
                structs.put(typeName, handler);
            }

            handler.fieldMap.put(fieldName, new JVMHandler.JVMField(fieldName, typeString, offset, statiz));
            entry += array;
        }

        return structs;
    }

    private List<JVMFlag> getFlags(Map<String, JVMType> types) {
        List<JVMFlag> jvmFlags = new ArrayList<>();

        JVMType flagType;

        if (types.containsKey("Flag")) flagType = types.get("Flag");
        else if (types.containsKey("JVMFlag")) flagType = types.get("JVMFlag");
        else throw new RuntimeException("NoVerify exception: Java flag 'Flag' not found");

        JVMHandler.JVMField flagsField;
        if (flagType.getFieldMap().containsKey("flags")) flagsField = flagType.getFieldMap().get("flags");
        else throw new RuntimeException("NoVerify exception: Java flag 'flags' not found");

        // Flags
        final long flags = unsafe.getAddress(flagsField.getOffset());

        JVMHandler.JVMField numFlagsField;
        if (flagType.getFieldMap().containsKey("numFlags")) numFlagsField = flagType.getFieldMap().get("numFlags");
        else throw new RuntimeException("NoVerify exception: Java flag 'Flag.numFlags' not found");

        // Num Flags
        final int numFlags = unsafe.getInt(numFlagsField.getOffset());

        JVMHandler.JVMField nameField;
        if (flagType.getFieldMap().containsKey("_name")) nameField = flagType.getFieldMap().get("_name");
        else throw new RuntimeException("NoVerify exception: Java flag 'Flag._name' not found");

        JVMHandler.JVMField addrField;
        if (flagType.getFieldMap().containsKey("_addr")) addrField = flagType.getFieldMap().get("_addr");
        else throw new RuntimeException("NoVerify exception: Java flag 'Flag._addr' not found");

        for (int i = 0; i < numFlags; i++) {
            final long flagAddress = flags + (i * flagType.size);
            final long flagNameAddress = unsafe.getAddress(flagAddress + nameField.offset);
            final long flagValueAddress = unsafe.getAddress(flagAddress + addrField.offset);

            final String flagName = parseOffset(flagNameAddress);

            if (flagName == null) continue;

            JVMFlag flag = new JVMFlag(flagName, flagValueAddress);
            jvmFlags.add(flag);
        }

        return jvmFlags;
    }

    public static void inject() {
        if (injected) return;

        try {
            Field unsafeMethod = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeMethod.setAccessible(true);
            unsafe = (Unsafe) unsafeMethod.get(null);
            nativeMethod = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);
            nativeMethod.setAccessible(true);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        FuckYouSkid noVerify = new FuckYouSkid();
        List<JVMFlag> flags = noVerify.getFlags(noVerify.getTypes(noVerify.getStructs()));

        for (JVMFlag flag : flags) {

            if(flag.name.toLowerCase().contains("attach") || flag.name.toLowerCase().contains("label"))
                System.out.println(flag.name);

            switch (flag.name) {
                case "com.ibm.tools.attach.enable":
                case "BytecodeVerificationLocal":
                case "BytecodeVerificationRemote":
                case "StartAttachListener": {
                    unsafe.putByte(flag.address, (byte) 0);
                    break;
                }
                case "DisableAttachMechanism" : {
                    unsafe.putByte(flag.address, (byte) 1);
                    break;
                }
                case "MaxLabelRootDepth": {
                    unsafe.putInt(flag.address, 1200);
                }
            }
        }

        final long pointer = findPointer(Pointers.STRUCT_DEFAULT);
        if (pointer > 0) unsafe.putByte(pointer, (byte) 0);

        injected = true;
    }
}
