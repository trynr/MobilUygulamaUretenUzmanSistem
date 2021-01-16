
public class ComponentInfo {
	// Kullanıcının eklediği componentlerin (EditText, Button vs.) bir değişken adı, tanımlanması için id'si ve kullanıcıya gösterilecek
	// bir text'i olmalıdır.
	String component_name;
	String component_id;
	String text;
	public ComponentInfo(String component_name, String component_id, String text) {
		super();
		this.component_name = component_name;
		this.component_id = component_id;
		this.text = text;
	}
	public String getComponent_name() {
		return component_name;
	}
	public void setComponent_name(String component_name) {
		this.component_name = component_name;
	}
	
	public String getComponent_id() {
		return component_id;
	}
	public void setComponent_id(String component_id) {
		this.component_id = component_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	} 
}
