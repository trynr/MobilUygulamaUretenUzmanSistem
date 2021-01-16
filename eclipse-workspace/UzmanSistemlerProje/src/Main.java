import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {
	static String java_code = "";  // Seçimler sonucu oluşturulacak java kodu
	static String xml_code = ""; // Seçimler sonucu oluşturulacak xml kodu
	static String user_class_code = ""; // Seçimler sonucu oluşturulacak User classı kodu. (Database'e kayıt işleminde kullanılıyor)
	static Set<String> imports; // Seçilen komponentler için gerekli package import satırları. Aynı importların tekrar yapılmaması için Set.
	static CodeSet codeset; // Gerekli kodların bulunduğu data set: CodeSet classı.
	static List<ComponentInfo> components; // User'ın uygulamasında bulunmasını istediği componentler
	static Set<String> functions;  // Kullanıcının butonlara atamış olduğu fonksiyonlar. Aynı fonksiyonların tekrar eklenmemesi için Set veriyapısı.
	static int components_count[]; // Hangi component'ten kaç tane olduğunun bilgisi. Component için id oluşturmada kullanılacak.
	static ArrayList<String> text_input_variables_from_app_user; // Text input alınacak edittext'lerin id bilgilerinin listesi.

	public static void main(String[] args) {
		// Tanımla 
		CodeSet codeSet = new CodeSet();
		imports = new HashSet<String>(); 
		components = new ArrayList<ComponentInfo>();
		functions = new HashSet<String>();
		text_input_variables_from_app_user = new ArrayList<String>();
		imports.add(CodeSet.imports_set.get("AppCompatActivity")); // Standart importlar
		imports.add(CodeSet.imports_set.get("Bundle"));
		imports.add(CodeSet.imports_set.get("View"));  // Butona fonksiyon vermek için gerekli importlar
		imports.add(CodeSet.imports_set.get("Toast"));
		
		beginUserClassCode();
		getInformationFromUser();
		createJavaAndXmlCode();
	}
	
	// Kullanıcıya hangi componentleri eklemek istediği sorulur ve component hakkında detaylı bilgi alınır.
	private static void getInformationFromUser() {
		int user_input = 0;
		String user_text_input = "";
		Scanner scanner = new Scanner(System.in);
		components_count = new int[4];  // Giriş, Seçim, Check, Button. Her biri 0 olarak başlatılacak, eklendikçe artırılacak. 

		do {
			System.out.println("Eklemek istediginiz componenti seciniz(Giris: 0, Secim: 1, Dogrula: 2, Buton: 3, Uygulamayı Tamamla: 4): ");
			user_input = scanner.nextInt();
			scanner.nextLine();  //for NextInt() bug

			if(user_input == 0) {  // Giriş
				System.out.println("Ne bilgisi alinsin?: ");
				user_text_input = scanner.nextLine();
				String component_name = "EditText";
				String component_id = createId(component_name, components_count);  // Component için gerekli id üretilir.
				components.add(new ComponentInfo(component_name, component_id, user_text_input));  // components listesine eklenir.
				components_count[0]++;  
				
			} else if(user_input == 1) { // Secim
				// Neyin seçilmesini istediğini ve seçeneklerin neler olduğu sorulur.
				System.out.println("Neyi sececek?: ");
				String choose = scanner.nextLine();
				String choose_from = "";
				do {
					System.out.println("Neler arasindan sececek?: (tamamlamak icin exit)");
					user_text_input = scanner.nextLine();
					if(!user_text_input.equals("exit")) {
						choose_from += user_text_input + ",";
					}
				} while(!user_text_input.equals("exit"));  // exit girmediği sürece seçenekler sorulur.
				
				String component_name = "RadioGroup";
				String component_id = createId(component_name, components_count);  // id oluşturulur.
				components.add(new ComponentInfo(component_name, component_id, choose + "," + choose_from)); // components'e eklenir.
				components_count[1]++;  

			} else if(user_input == 2) {  // Dogrula
				// Checkbox oluşturulur, neyi doğrulayacağı sorulur.
				System.out.println("Neyi dogrula?: ");
				String check_info = scanner.nextLine();
				String component_name = "CheckBox";
				String component_id = createId(component_name, components_count);  // id oluşturulur.
				components.add(new ComponentInfo(component_name, component_id, check_info)); //components'e eklenir.
				components_count[2]++;

			} else if(user_input == 3) {  // Button
				print_button_functions(); // Hangi fonksiyonları atayabileceği gösterilir ve sorulur.
				System.out.println("Fonksiyonu ne olsun?: ");
				int button_function = scanner.nextInt(); scanner.nextLine();
				
				String func_name = getFuncName(button_function); // String karşılığını al kullanıcının girdiği sayının.
				String function_info = "";
				
				if(button_function == 1) { // Bastir
					functions.add(CodeSet.functions_set.get("bastir"));  // bastir fonksiyonu koda eklenir.
					// Bastırılacak text'te user'dan alınan girdiler kullanılabilir, bu girdi alanları seçenek olarak sunulur.
					// Örn. 1: ad  2: soyad          US kullanıcısı "Merhaba 1 2"  yazarak user'ın ad soyadının kullanılmasını sağlar.
					int i = 0;	
					for(ComponentInfo cmpInfo: components) {
						if(cmpInfo.getComponent_name().equals("EditText")) {
							System.out.println(i + ": " + cmpInfo.getText());
							i++;
						}
					}
					System.out.println("Ne bastirilsin? ");
					
					function_info = scanner.nextLine();
				} 				
				else if(button_function == 2) {  // Db'ye kaydet 
					// Firebase database için gerekli importlar yapılır.
					imports.add(CodeSet.imports_set.get("FirebaseFirestore"));
					imports.add(CodeSet.imports_set.get("OnSuccessListener"));
					imports.add(CodeSet.imports_set.get("DocumentReference"));
					imports.add(CodeSet.imports_set.get("DocumentSnapshot"));

					// db_kaydet fonksiyonu koda eklenir.
					functions.add(CodeSet.functions_set.get("db_kaydet"));					
				}
				else { // Aritmetik işlemler
					functions.add(CodeSet.functions_set.get("bastir")); // Aritmetik işlemin sonucunun bastırılması için bastir fonk. koda eklenir.
					String func = "";
					String sayi1 = "", sayi2 = "";
					
					switch(button_function) {
						case 3: func = "topla"; break;
						case 4: func = "cikar"; break;
						case 5: func = "carp"; break;					
						case 6: func = "bol"; break;
					}
					functions.add(CodeSet.functions_set.get(func)); // gerekli aritmetik işlem koda eklenir.
				
					// Aritmetik işlemin yapılması için direkt bir değer veya user girdisinin olacağı alan alınır. 
					for(int i = 0; i < 2; i++) {
						char ch = 'b';	
						System.out.println("a: Bir deger vermek icin");
						for(ComponentInfo cmpInfo: components) {
							if(cmpInfo.getComponent_name().equals("EditText")) {
								System.out.println(ch + ": " + cmpInfo.getText());
								ch++;
							}
						}
						
						System.out.println("Giris yapiniz: ");
						String sayi_str = scanner.nextLine();
						
						if(sayi_str.equals("a")) {
							System.out.println("Sayi " + (i+1));							
							if(i == 0) sayi1 = scanner.nextLine();
							if(i == 1) sayi2 = scanner.nextLine();
						}
						else {
							if(i == 0) sayi1 = sayi_str;
							if(i == 1) sayi2 = sayi_str;
						}
						
					}
								
					function_info = sayi1 + "," + sayi2;
				}
							
				function_info = func_name + "," + function_info; // Fonksiyon adı,parametre1,parametre2... şeklinde tutuluyor olacak.
				
				String component_name = "Button";
				String component_id = createId(component_name, components_count);  // Button için id üretilir.
				components.add(new ComponentInfo(component_name, component_id, function_info)); // components listesine eklenir.
				components_count[3]++;

			}
			
		} while(user_input != 4);
		
	}
	
	// Seçimlere göre java ve xml kodları üretilir.
	private static void createJavaAndXmlCode() {
		beginJavaCode();  // Standart java kodu başlangıcı java_code değişkenine atanır. 
		beginXmlCode();  // Standart xml kodu başlangıcı xml_code değişkenien atanır.
		// Import kodları, program kodu ve function kodları. Sıralı bir şekilde olması için önce tutulup daha sonra sırayla code'a eklenecek.
		String java_program_code = "", java_import_code = "", java_button_functions_code = ""; 
		
		// User.java'nın oluşturulması için gerekli kodlar üretilerek değişkenlerde tutulur.
		String user_class_constructor_parameters_code = "";
		String user_class_constructor_inner_code = "";
		String user_class_getter_setters_code = "";
		
		for(ComponentInfo componentInfo: components) {
			String component_name = componentInfo.getComponent_name(); // Component'in değişken ismi 
			String component_id = componentInfo.getComponent_id(); // Component'in id'si
			String component_hint = componentInfo.getText();  // Component'in kullanıcıya gösterilecek hint'i
			
			// Componentlerin java tarafında değişkenlere atanması için gerekli kod. findViewById kullanılır.
			java_program_code += "\t\tfinal " + component_name + " " + component_id + " = findViewById(R.id." + component_id + ");\n";
			xml_code += "\t" + createXmlCodeForGivenInfo(component_id, componentInfo, components_count);  // xml code oluşturulur component'e göre.
			imports.add(CodeSet.imports_set.get(component_name));   // Eklenmiş componentler için gerekli importlar yapılır.
			
			if(component_name.equals("EditText")) {  // Component bir EditText ise 
				// Alınmak istenen text user classına String olarak eklenir.
				String variable_name = removeWhiteSpaces(component_hint);
				user_class_code += "\t" + "String " + variable_name + ";\n";
				user_class_constructor_parameters_code +=  "String " + variable_name + ",";
				user_class_constructor_inner_code += "\t\t" + "this." + variable_name + " = " + variable_name + ";\n";
				user_class_getter_setters_code += "\tpublic String get" + variable_name + "(){\n" 
											    + "\t\treturn " + variable_name 
											    + ";\n"
											    + "\t}\n"; 
				user_class_getter_setters_code += "\tpublic void set" + variable_name + "(String " + variable_name + ") {\n"
											+ "\t\tthis." + variable_name + " = " + variable_name + ";\n" 
											+ "\t}\n";
			} else if(component_name.equals("RadioGroup")) {  // Component bir RadioGroup ise
				// Seçenekler kullanıcıdan alınmış ve arada virgüllerle component_hint'de depolanmıştı.
				// Burada seçenekler teker teker alınır ve RadioButtonlar olarak eklenir.
				imports.add(CodeSet.imports_set.get("RadioButton"));
				String str[] = component_hint.split(","); // Virgüllerle ayrılmış elemanların ilki değişkenin ismi olacak.
				user_class_code += "\tString " + removeWhiteSpaces(str[0]) + ";\n";
				String variable_name = removeWhiteSpaces(str[0]);
				user_class_constructor_parameters_code += "String " + variable_name + ",";
				user_class_constructor_inner_code += "\t\t" + "this." + variable_name + " = " + variable_name + ";\n";
				user_class_getter_setters_code += "\tpublic String get" + variable_name + "(){\n" 
					    + "\t\treturn " + variable_name 
					    + ";\n"
					    + "\t}\n"; 
				user_class_getter_setters_code += "\tpublic void set" + variable_name + "(String " + variable_name + ") {\n"
					+ "\t\tthis." + variable_name + " = " + variable_name + ";\n" 
					+ "\t}\n";
			} else if(component_name.equals("CheckBox")) { // Component bir Checkbox ise
				// User'ın check etmesi veya etmemesi üzerinden değerlendirilir, user classında boolean bir değer olarak tutulur.
				String variable_name = removeWhiteSpaces(component_hint);
				user_class_code += "\tboolean is_" + variable_name + ";\n";
				user_class_constructor_parameters_code += "boolean is_" + variable_name + ",";
				user_class_constructor_inner_code += "\t\t" + "this.is_" + variable_name + " = is_" + variable_name + ";\n";
				user_class_getter_setters_code += "\tpublic boolean isIs_" + variable_name + "(){\n" 
					    + "\t\treturn is_" + variable_name 
					    + ";\n"
					    + "\t}\n"; 
				user_class_getter_setters_code += "\tpublic void set" + variable_name + "(boolean is_" + variable_name + ") {\n"
					+ "\t\tthis.is_" + variable_name + " = is_" + variable_name + ";\n" 
					+ "\t}\n";
			} else if(component_name.equals("Button")) {				
				// Button ise fonksiyonu da var demek. Butona basıldığında ne yapılacağı koda eklenmeli.
				String button_click_code[] = CodeSet.createButtonClickCode(component_id);
				String func_name_and_parameters[] = componentInfo.getText().split(",");  // func_name, arg1, arg2, ... şeklinde tutuluyordu.
				String func_name = func_name_and_parameters[0];
				String user_answers_in_ets = ""; // user'ın cevaplarını al.
				String func_content = "";
				
				if(func_name.equals("bastir")) { // Atanmak istenen fonksiyon bastirma işlemi ise
					// Butona basıldığında EditTextlere user'ın doldurmuş olduğu cevaplar alınmalı,
					text_input_variables_from_app_user.clear();

					for(ComponentInfo cmpInfo: components) { // Tüm butonlar için tüm componentleri gez. EditText olanların içeriğini al.
						String cmp_name = cmpInfo.getComponent_name();
						String cmp_id = cmpInfo.getComponent_id();
						if(cmp_name.equals("EditText")) {
							String str_variable = cmp_id.toLowerCase() + "_str";
							text_input_variables_from_app_user.add(str_variable);
							user_answers_in_ets += "\t\t\t\tString " + str_variable + " = " 
											+ cmp_id.toLowerCase() + ".getText().toString();\n";
						}
					}
					
					func_content = func_name + "(";    //func_name bastir vs.  bastir("Merhaba kullanıcı");
					String text_to_show = "";
					int i = 0; 
					String texts[] = func_name_and_parameters[1].split(" "); // Bastır için text "Merhaba 1 2" -> Merhaba, ad, soyad
					while(i < texts.length) {
						if(!text_to_show.equals("")) text_to_show += " + "; // İlk ekleme harici başına + ekle.
						
						if(onlyDigits(texts[i])) {
							int app_user_input_index = Integer.valueOf(texts[i]);
							text_to_show += text_input_variables_from_app_user.get(app_user_input_index) + " + \" \"";	
						} else {
							text_to_show += "\"" + texts[i] + " \"";
						}					
			
						i++;
					}
					func_content += text_to_show + ");";
					
					String button_func_code =            // Butona listener atamayı hazır olarak al, gerekli yere istenen işlevi koy.
							button_click_code[0] +      
							user_answers_in_ets + 
							"\t\t\t\t" + func_content + "\n" + 
							button_click_code[1];
					java_button_functions_code += button_func_code + "\n";   // setOnClickListener kodu
				}
				else if(func_name.equals("db_kaydet")) {
					// db_kaydet seçeneğinde  1.user'ın inputları değişkenlere alınacak. 2.User classından obje alınacak bu değişkenlerle.
					// 3. db_kaydet metodu çağrılacak. 
					List<String> input_variables = new ArrayList<String>();
					
					for(ComponentInfo cmpInfo: components) { 
						// Tüm componentlerden bilgi al. EditText'lerdeki girdiler, RadioGroup'ta seçilmiş seçenek.
						String cmp_name = cmpInfo.getComponent_name();
						String cmp_id = cmpInfo.getComponent_id();
						if(cmp_name.equals("EditText")) {
							String str_variable = cmp_id.toLowerCase() + "_str";
							input_variables.add(str_variable);
							user_answers_in_ets += "\t\t\t\t"+
											"String " + str_variable + " = " 
											+ cmp_id.toLowerCase() + ".getText().toString();\n";
						} else if(cmp_name.equals("RadioGroup")) {
							Random rnd = new Random();
							int id_num = rnd.nextInt(100);
							String str_variable = cmp_id.toLowerCase() + "_str";
							input_variables.add(str_variable);
							user_answers_in_ets += "\t\t\t\t"+						
									"RadioButton radioButton" + id_num + " = findViewById("+cmp_id+".getCheckedRadioButtonId());\r\n" + 
									"String " + str_variable + " = " + "radioButton" + id_num + ".getText().toString();\n";
									
						} else if(cmp_name.equals("CheckBox")) {
							String bool_variable = "is_" + removeWhiteSpaces(cmpInfo.getText());
							input_variables.add(bool_variable);
							user_answers_in_ets += "\t\t\t\t"+
												   "boolean " + bool_variable + " = " + cmp_id + ".isChecked();\n";
						}
					}
					
					// User objesini oluştur.
					String create_user_code = "\t\t\t\t" + "User user = new User(";
					String arguments = "";
					int i = 0;
					for(String str : input_variables) {
						arguments += str;
						if(input_variables.size()-1 != i) arguments += ","; 
						i++;
					}
					create_user_code += arguments;
					create_user_code += ");\n";
							
					func_content = func_name + "(user);\n";   // db_kaydet(user) 
					
					
					String button_func_code = 
							button_click_code[0] + 
							user_answers_in_ets + 
							create_user_code + 
							"\t\t\t\t" + func_content + "\n" + 
							button_click_code[1];
					java_button_functions_code += button_func_code + "\n";   // setOnClickListener kodu
				}
				else { // Aritmetik işlemler için kod üretme işlemi
					// Butona basıldığında EditTextlere user'ın doldurmuş olduğu cevaplar alınmalı,
					text_input_variables_from_app_user.clear();

					int k = 0;
					for(ComponentInfo cmpInfo: components) { // Tüm butonlar için tüm componentleri gez. EditText olanların içeriğini al.
						String cmp_name = cmpInfo.getComponent_name();
						String cmp_id = cmpInfo.getComponent_id();
						if(cmp_name.equals("EditText")) {
							String str_variable = cmp_id.toLowerCase() + "_integer";
							text_input_variables_from_app_user.add(str_variable);
							user_answers_in_ets += "\t\t\t\t" + "Integer " + str_variable + " = 0;\n";
							user_answers_in_ets += "\t\t\t\t" + "try{\n";
							user_answers_in_ets += "\t\t\t\t\t" + str_variable + " = Integer.valueOf(" 
											+ cmp_id.toLowerCase() + ".getText().toString());\n";
							user_answers_in_ets += "\t\t\t\t" + "}catch(NumberFormatException e){e.printStackTrace();}\n";
						}
						k++;
					}
					
					
					func_content = "bastir(\"Sonuc: \" + ";
					
					func_content += func_name + "(";    //func_name topla carp vs.  cikar(200, dogum_tarihi.getText()); gibi.
					String parameter_text = "";
					int i = 0; 
					String parameters = func_name_and_parameters[1] + "," + func_name_and_parameters[2];
					String texts[] = parameters.split(",");
					while(i < 2) {
						if(i == 1) parameter_text += " , ";
						
						if(onlyChars(texts[i])) {
							char app_user_input = texts[i].charAt(0);
							int app_user_index = app_user_input - 'b';
							parameter_text += text_input_variables_from_app_user.get(app_user_index);	
						} else {
							parameter_text += texts[i];
						}	
						
						i++;
					}
					
					func_content += parameter_text + ")";
					func_content += ");";
					
					String button_func_code = 
							button_click_code[0] + 
							user_answers_in_ets + 
							"\t\t\t\t" + func_content + "\n" + 
							button_click_code[1];
					java_button_functions_code += button_func_code + "\n";   // setOnClickListener kodu
					
				}

			} 
		}
		
		for(String imp: imports) {   // Kodda kullanılmış componentler için gerekli package'lar import edilir.
			java_code += imp + "\n";
		}
		java_code += CodeSet.java_program[0] + "\n" + CodeSet.java_program[1] + "\n"; // Beginning of the code
		java_code += java_program_code;
		java_code += java_button_functions_code;
		
		endJavaCode();
		endXmlCode();
		endUserClassCode(user_class_constructor_parameters_code, user_class_constructor_inner_code, user_class_getter_setters_code);
	}
	
	// Component için gerekli id'yi oluşturacak.
	private static String createId(String component, int components_count[]) {
		String components[] = new String[] {"EditText", "RadioGroup", "CheckBox", "Button"};
		int comp_index = 0;
		
		for(String comp: components) { 
			if(comp.equals(component)) {
				break;
			}
			comp_index++;
		}
		
		String component_id = component.toLowerCase() + (components_count[comp_index]+1);
	
		return component_id;
	}

	private static void beginJavaCode() {
		java_code = CodeSet.pack[1] + "\n";   // Package'ın import edilmesi ile initialize edilir.
	}
	private static void endJavaCode() {
		java_code += CodeSet.java_program[2] + "\n"; 
		
		for(String func: functions) {   // Butonlara verilmiş fonksiyonlar import edilir.
			java_code += func + "\n";
		}
		
		java_code += CodeSet.java_program[3]; // End of the code
		
		writeCodeToFile("MainActivity.java", java_code);
		System.out.println(java_code); // Test için
	}
	private static void beginXmlCode() {
		xml_code += CodeSet.xml_program[0] + "\n";
	}
	private static void endXmlCode() {
		xml_code += CodeSet.xml_program[1];
		writeCodeToFile("activity_main.xml", xml_code);
		
		System.out.println(xml_code); // Test i
	}
	private static void beginUserClassCode() {
		user_class_code += "package com.ynr.keypsd.mobiluygulamauretenuzmansistem;\r\n"+
						   "import java.io.Serializable;\r\n" + 
						   "public class User implements Serializable{\r\n";
	}
	private static void endUserClassCode(String user_class_constructor_parameters_code, String user_class_constructor_inner_code, String user_class_getter_setters_code){
		// Constructor ekle.
		if(user_class_constructor_parameters_code.charAt(user_class_constructor_parameters_code.length()-1) == ',')
			user_class_constructor_parameters_code = charRemoveAt(user_class_constructor_parameters_code, user_class_constructor_parameters_code.length()-1); // Son karakter virgül ise kaldır.
		
		user_class_code += "\n";
		user_class_code += "\tpublic User(" + user_class_constructor_parameters_code + "){\n";
		user_class_code += user_class_constructor_inner_code;
		user_class_code += "\t}\n";		// Constructor kodunu tamamla.
		
		user_class_code += user_class_getter_setters_code; // Getter setter kodlarını ekle.
		
		user_class_code += "}"; 		// User class kodunu tamamla.
		
		System.out.println(user_class_code);
		writeCodeToFile("User.java", user_class_code);
	}
	private static String createXmlCodeForGivenInfo(String id, ComponentInfo componentInfo, int components_count[]) {
		String xml_code = "";
		String component_name = componentInfo.getComponent_name();
		String hint = componentInfo.getText();
		
		if(component_name.equals("EditText")) {
			xml_code = CodeSet.createEditText(id, hint);
		} else if(component_name.equals("RadioGroup")) {
			xml_code = CodeSet.createRadioGroup(id, hint); 
			// Kaçıncı RadioGroup olduğu bilgisi de verilmeli -> Alt componentler olan RadioButtonların id'sini belirlerken kullanılacak.
		} else if(component_name.equals("CheckBox")) {
			xml_code = CodeSet.createCheckBox(id, hint);
		} else if(component_name.equals("Button")) {
			String func_name_and_args[] = hint.split(",");
			String button_text = "";
			if(func_name_and_args[0].equals("bastir")) button_text = "Mesaj";
			else if(func_name_and_args[0].equals("db_kaydet")) button_text = "Kaydet";
			else button_text = "Hesapla";
			
			xml_code = CodeSet.createButton(id, button_text);
		} 
		
		return xml_code;
	}
	private static String createUserClassCode() {
	
		return "";
	}
	
	
	private static void writeCodeToFile(String file_name, String code_text) {
		File file = new File(file_name);		
        FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file, false);
			BufferedWriter bWriter = new BufferedWriter(fileWriter);
		    bWriter.write(code_text);
		    bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static void print_button_functions() {		
	    List<Map.Entry<String, String>> entries = new ArrayList<>(CodeSet.functions_set.entrySet());		
	    int i = 1;
	    
	    for (Map.Entry<String, String> entry : entries) {
	        System.out.println(i+ ": "+ entry.getKey());
	        i++;
	    }
		
	}
	
	private static String getFuncName(int button_function) {
		Set<String> keys;
		keys = CodeSet.functions_set.keySet();
		String func_name = "";
		int i = 0;
		
		for(String key: keys) {
			if(i == button_function-1) {
				func_name = key;
				break;
			}
			i++;
		}	
		
		return func_name;
	}
	
	// Bir string sadece sayılar mı içeriyor kontrolü yapan fonksiyon.
	// Merhaba 1 2 -> Merhaba ad soyad'a çevrilerek kullanıcının ad - soyad bölümlerindeki girdileri kullanılacak.
    public static boolean onlyDigits(String str)  { 

        for (int i = 0; i < str.length(); i++) { 
            if (str.charAt(i) < '0' || str.charAt(i) > '9') { 
                return false;  // digit olmayan bir eleman var ise false dön ve çık.
            } 
        } 
        
        return true; 
    } 
    
    // Sadece bir karakter mi içeriyor kontrolü.
    // 2020, d     Karakter girmişse karşılık gelen edit text'ten değer alınacak. 
    public static boolean onlyChars(String str)  { 
    	if(str.length() == 1 && str.charAt(0) > 'a' && str.charAt(0) < 'z') {
    		return true;
    	}
        
        return false; 
    } 
    
	
    // ID oluştururken Boşlukları kaldırma amaçlı.
    public static String removeWhiteSpaces(String str){
        String s = "";
        str = clearPunctuation(str);
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            int temp = arr[i];
            if(temp != 32 && temp != 9) { // 32 ASCII for space and 9 is for Tab
                s += arr[i];
            }
        }
        return s;
    }
    
    // Belli bir yerdeki karakteri kaldır. Parametreleri oluştururken son karakter virgül ise kaldır.
    public static String charRemoveAt(String str, int p) {
        return str.substring(0, p) + str.substring(p + 1);
    }
    
    
    // ID oluştururken noktalama işaretlerini kaldır.
    public static String clearPunctuation(String str){

        for(int k=str.length()-1; k>=0; k--){
            if(str.charAt(k)=='-'||str.charAt(k)=='.'||str.charAt(k)==8230||str.charAt(k)=='['||str.charAt(k)==']'||str.charAt(k)=='\''){
                str = charRemoveAt(str, k);
            }

        }

        return str;
    }
    
    
	
}
