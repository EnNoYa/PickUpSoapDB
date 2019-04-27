using UnityEngine;
using System.Collections;

public class Bluetooth : AndroidBehaviour<Bluetooth>
{
    protected override string javaClassName
    {
        get { return "com.example.androidbluetooth.MainActivity"; }
    }


    public static void openBT()
    {
        instance.CallStatic("openBluetooth");
    }
}
