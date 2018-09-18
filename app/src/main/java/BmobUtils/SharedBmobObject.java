package BmobUtils;

// 保存当前用户在Bmob云端的信息
public class SharedBmobObject {
    //private static SharedBmobObject sSharedBmobObject;

    public static String sUserName;
    public static String sToDoObjectId;
    public static String sThoughtObjectId;
    public static String sReadObjectId;

    public static boolean uploadDone = true;
    public static boolean downloadDone = true;

//    public static SharedBmobObject get(){
//        if(sSharedBmobObject == null){
//            sSharedBmobObject = new SharedBmobObject();
//        }
//        return sSharedBmobObject;
//    }
//
//    private SharedBmobObject(){
//        sUserName = "";
//        sToDoObjectId = "";
//    }
}
