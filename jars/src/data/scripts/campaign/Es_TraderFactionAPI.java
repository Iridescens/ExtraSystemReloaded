package data.scripts.campaign;

public  class Es_TraderFactionAPI {
	private String id;
	private String name;
	private String hints;//简称
	private String signal;//标记
	private float quality_f;//品质加成因素
	private float quality_r;//品质浮动因素
	private float price_f;//价格因素
	private float price_r;//价格浮动因素
	private float time_pre_f;//发货时间因素
	private float time_del_f;//送货时间因素
	public Es_TraderFactionAPI(String id,String name,String hints,String signal,float quality_f,float quality_r,float price_f,float price_r,float time_pre_f,float time_del_f){
		this.id = id;
		this.name = name;
		this.hints = hints;
		this.signal = signal;
		this.quality_f =quality_f;
		this.quality_r =quality_r;
		this.price_f = price_f;
		this.price_r = price_r;
		this.time_pre_f = time_pre_f;
		this.time_del_f = time_del_f;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getHints() {
		return hints;
	}
	public String getSignal() {
		return signal;
	}
	public float getQuality_f() {
		return quality_f;
	}
	public float getQuality_r() {
		return quality_r;
	}
	public float getPrice_f() {
		return price_f;
	}
	public float getPrice_r() {
		return price_r;
	}
	public float getTime_pre_f() {
		return time_pre_f;
	}
	public float getTime_del_f() {
		return time_del_f;
	}
	
}
