package yk.authentication.domain;

public class User {
	private String name;
	private String sex;
	private String id;
	private String tel;
	private String wechat;//΢��OPENID����֤�û���ʾ��֮�󶨵�΢�źţ�����ͻ���ʾ��������֤�û���΢�ź�
	private String hospital;//ԤԼҽԺ
	private String department;//ԤԼ����
	private String doctor;//ԤԼҽ��
	private String disease;//���Ｒ��
	private String time;//ԤԼʱ��
	private String code;//������Կ
	private String expdate;//������Ч��
	private int usetimes;//������Կʹ�ô���
	private int seq;//ԤԼ������
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getExpdate() {
		return expdate;
	}
	public void setExpdate(String expdate) {
		this.expdate = expdate;
	}
	public int getUsetimes() {
		return usetimes;
	}
	public void setUsetimes(int usetimes) {
		this.usetimes = usetimes;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getDoctor() {
		return doctor;
	}
	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}
	public String getDisease() {
		return disease;
	}
	public void setDisease(String disease) {
		this.disease = disease;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
}
