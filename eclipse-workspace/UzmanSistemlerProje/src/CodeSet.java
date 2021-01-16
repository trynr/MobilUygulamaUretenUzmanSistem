import java.util.HashMap;
import java.util.LinkedHashMap;

public class CodeSet {
	
	static String pack[] = new String[] {"package", "package com.ynr.keypsd.mobiluygulamauretenuzmansistem;"}; // package'ı belirten satır
	
	static LinkedHashMap<String, String> imports_set;	 // Kütüphane import satırları
	static LinkedHashMap<String, String> functions_set; // Butona verilecek fonksiyonlar.
	
	public CodeSet() {
		createImportsSet();
		createFunctionsSet();
	}

	// Kütüphane import satırlarını oluştur.
	public void createImportsSet() {
		imports_set = new LinkedHashMap<String, String>();
		imports_set.put("AppCompatActivity", "import androidx.appcompat.app.AppCompatActivity;");
		imports_set.put("Bundle", "import android.os.Bundle;");
		imports_set.put("EditText", "import android.widget.EditText;");
		imports_set.put("CheckBox", "import android.widget.CheckBox;");
		imports_set.put("RadioGroup", "import android.widget.RadioGroup;");
		imports_set.put("RadioButton", "import android.widget.RadioButton;");
		imports_set.put("Button", "import android.widget.Button;");
		imports_set.put("View", "import android.view.View;");
		imports_set.put("Toast", "import android.widget.Toast;");
		imports_set.put("RadioButton", "import android.widget.RadioButton;");
		imports_set.put("FirebaseFirestore", "import com.google.firebase.firestore.FirebaseFirestore;");
		imports_set.put("OnSuccessListener", "import com.google.android.gms.tasks.OnSuccessListener;");
		imports_set.put("DocumentReference", "import com.google.firebase.firestore.DocumentReference;");
		imports_set.put("DocumentSnapshot", "import com.google.firebase.firestore.DocumentSnapshot;");
		
	}
	
	// Butona verilebilecek fonksiyonlar.
	public void createFunctionsSet() { 
		functions_set = new LinkedHashMap<String, String>();
		functions_set.put("bastir", "private void bastir(String text){\n"
									+"\tToast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();\r\n" 
									+"}");
		functions_set.put("db_kaydet", "private void db_kaydet(User user){\n" + 
				"\tFirebaseFirestore fStore = FirebaseFirestore.getInstance();\r\n" + 
				"\tfinal DocumentReference documentReference = fStore.collection(\"Users\").document(\"1\");\r\n" + 
				"\tfinal User finalUser = user;\r\n" + 
				"\tdocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {\r\n" + 
				"\t\t@Override\r\n" + 
				"\t\tpublic void onSuccess(DocumentSnapshot documentSnapshot) {\r\n" + 
				"\t\t\tdocumentReference.set(finalUser);\r\n" + 
				"\t\t}\r\n" + 
				"\t}\r\n" + 
				"\t);\n" +
				"}");
		functions_set.put("topla", "private int topla(int sayi1, int sayi2){\r\n" + 
				"\treturn sayi1 + sayi2;\r\n" + 
				"}");
		functions_set.put("cikar", "private int cikar(int sayi1, int sayi2){\r\n" + 
				"\treturn sayi1-sayi2;\r\n" + 
				"}");
		functions_set.put("carp", "private int carp(int sayi1, int sayi2){\r\n" + 
				"\treturn sayi1*sayi2;\r\n" + 
				"}");
		functions_set.put("bol", "private int bol(int sayi1, int sayi2){\r\n" + 
				"\treturn sayi1/sayi2;\r\n" + 
				"}");		
	}
	
	
	// XML tasarım kodu - EditText üretimi. id'si ve kullanıcıya gösterilecek ipucu verilir.
	public static String createEditText(String id, String hint) {
		return "<EditText\r\n" + 
		"\t\tandroid:layout_width=\"match_parent\"\r\n" + 
		"\t\tandroid:id=\"@+id/" + id +"\"\r\n" + 
		"\t\tandroid:hint=\"" + hint + "\"\r\n" + 
		"\t\tandroid:layout_margin=\"10dp\"\r\n" + 
		"\t\tandroid:padding=\"10dp\"\r\n" + 
		"\t\tandroid:layout_height=\"wrap_content\"/>\n";
	}
	
	// RadioGroup üretimi. Kullanıcıdan seçenekler ve neyin seçileceği bilgisi alınır.
	public static String createRadioGroup(String id, String all_texts) {
		int radioGroup_id = 0;
		radioGroup_id = Integer.valueOf(id.charAt(id.length()-1));
		
		String code = "";
		String texts[] = all_texts.split(","); 
		String header = texts[0];
		int option_count = 0;
		for(int i = 1; i < texts.length; i++) {
			option_count++;
		}
		
		code += "<RadioGroup\n" + 
				"\t\tandroid:layout_width=\"match_parent\"\r\n" + 
				"\t\tandroid:id=\"@+id/"+id+"\"\r\n" + 
				"\t\tandroid:orientation=\"vertical\"\r\n" + 
				"\t\tandroid:layout_margin=\"10dp\"\r\n" + 
				"\t\tandroid:padding=\"10dp\"\r\n" + 
				"\t\tandroid:layout_height=\"wrap_content\">\r\n" +  
				"\t<TextView\r\n" + 
				"\t\tandroid:textSize=\"18sp\"\r\n" + 
				"\t\tandroid:layout_margin=\"5dp\"\r\n" + 
				"\t\tandroid:layout_width=\"wrap_content\"\r\n" + 
				"\t\tandroid:layout_height=\"wrap_content\"\r\n" + 
				"\t\tandroid:text=\"" + header + "\"/>\n";
	
		for(int i = 0; i < option_count; i++) {
			code += "\t\t<RadioButton\r\n" + 
					"\t\t\tandroid:id=\"@+id/radioButton" + (radioGroup_id+1) + (i+1) + "\"\r\n"+
					"\t\t\tandroid:textSize=\"16sp\"\r\n" + 
					"\t\t\tandroid:layout_width=\"wrap_content\"\r\n" + 
					"\t\t\tandroid:text=\"" + texts[1+i] + "\"\r\n" + 
					"\t\t\tandroid:layout_height=\"wrap_content\"/>\r\n";
		}
		
		code += "\t</RadioGroup>\n";
		
		return code;
	}
	
	// Checkbox üretilir.
	public static String createCheckBox(String id, String hint) {
		return "<CheckBox\r\n" + 
				"\t\tandroid:layout_width=\"match_parent\"\r\n" + 
				"\t\tandroid:id=\"@+id/" + id +"\"\r\n" +
				"\t\tandroid:hint=\"" + hint + "\"\r\n" + 
				"\t\tandroid:layout_margin=\"10dp\"\r\n" + 
				"\t\tandroid:padding=\"10dp\"\r\n" + 
				"\t\tandroid:layout_height=\"wrap_content\"/>\n";
	}
	
	// Button üretilir.
	public static String createButton(String id, String text) {
		return  "<Button\r\n" + 
				"\t\tandroid:layout_width=\"match_parent\"\r\n" + 
				"\t\tandroid:id=\"@+id/" + id + "\"\r\n" + 
				"\t\tandroid:text=\"" + text + "\"\r\n" + 
				"\t\tandroid:textSize=\"18sp\"\r\n" + 
				"\t\tandroid:layout_margin=\"10dp\"\r\n" + 
				"\t\tandroid:padding=\"10dp\"\r\n" + 
				"\t\tandroid:layout_height=\"wrap_content\"/>\r\n";
	}
	
	// Butonlara listener atama kodu.
	public static String[] createButtonClickCode(String button_id) {
		String[] code = new String[] {
				"\t\t"+ button_id +".setOnClickListener(new View.OnClickListener() {\r\n" + 
						   "\t\t\t@Override\r\n" + 
						   "\t\t\tpublic void onClick(View v) {\n" ,	
						   
						   	"\t\t\t}\r\n" + 
						   	"\t\t});"
		};
		
		return code;
	}
	
	// Java dosyasının genel yapısı.
	static String java_program[] = new String[] {"public class MainActivity extends AppCompatActivity {",
									 "\t@Override\n" + 
									 "\tprotected void onCreate(Bundle savedInstanceState) {\n" + 
									 "\t\tsuper.onCreate(savedInstanceState);\n" + 
									 "\t\tsetContentView(R.layout.activity_main);",
									 "\n\t}",
									 "\n}"
									 };
	
	// XML dosyasının genel yapısı.
	static String xml_program[] = new String[] {
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
			"<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\r\n" + 
			"    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\r\n" + 
			"    xmlns:tools=\"http://schemas.android.com/tools\"\r\n" + 
			"    android:layout_width=\"match_parent\"\r\n" + 
			"    android:orientation=\"vertical\"\r\n" + 
			"    android:layout_height=\"match_parent\"\r\n" + 
			"    tools:context=\".MainActivity\">\r\n" + 
			"    \r\n" + 
			"    <ScrollView\r\n" + 
			"        android:layout_width=\"match_parent\"\r\n" + 
			"        android:layout_height=\"wrap_content\">\r\n" + 
			"        <LinearLayout\r\n" + 
			"            android:layout_width=\"match_parent\"\r\n" + 
			"            android:orientation=\"vertical\"\r\n" + 
			"            android:layout_height=\"wrap_content\">\n",
			
			"        </LinearLayout>\r\n" + 
			"    </ScrollView>\r\n" + 
			"\r\n" + 
			"</LinearLayout>"
	};
	
	
}
