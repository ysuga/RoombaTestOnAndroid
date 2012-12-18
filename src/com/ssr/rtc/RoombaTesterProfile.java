package com.ssr.rtc;

/**
 * コンポーネントのコンフィグレーションを定義
 */
public class RoombaTesterProfile {
	public static final String DefaultNameServer= "192.168.11.9";		// host[:port]
	public static final String Name				= "RoombaTesterAndroid";
	public static final String ImplementationId	= "RoombaTesterAndroid_instance";
	public static final String Type				= "DataFlowComponent";
	public static final String Description		= "Sample RTC on Android for Roomba Control";
	public static final String Version			= "1.0";
	public static final String Vendor			= "SUGAR SWEET ROBOTICS Co., Ltd.";
	public static final String Category			= "Experimental";

	public static final float execute_rate		= 5F; // 秒間何回処理をするか (wait = 1.0 / execute_rate * 1000000 usec)

	//
	// コンポーネントのコンフィグレーションを定義
	//
	//public static final String ConfigName1		= "S_multiple_coefficient";
	//public static final String ConfigName2		= "S_float_sample";
	//public static final String ConfigName3		= "S_string_sample_1";
	//public static final String ConfigName4		= "S_string_sample_2";

	//
	// コンポーネントのデータポートを定義
	//
	// << in port >>
	public static final String InPort1			= "S_in_port_1";

	// << out port >>
	public static final String OutPort1			= "S_out_port_1";
}
