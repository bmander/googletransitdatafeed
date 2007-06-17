/*
 * Copyright 2007 GoogleTransitDataFeed
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.GTDF.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.core.client.GWT; 
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;

import com.GTDF.client.Transxchange2GoogleTransitService;
import com.GTDF.client.Transxchange2GoogleTransitServiceAsync;

import com.google.gwt.user.client.rpc.InvocationException;

/*
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Transxchange2GoogleTransit implements EntryPoint {

	/*
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		String wikiUserName = readUsername(); 

		final TextBox textBoxServiceStartGz = new TextBox();
		final TextBox textBoxUrl = new TextBox();
		textBoxUrl.setText("http://www.aagency.org");
		final ListBox listBoxTimezoneP = new ListBox();
		listBoxTimezoneP.addItem("Africa");
		listBoxTimezoneP.addItem("America");
		listBoxTimezoneP.addItem("Antarctica");
		listBoxTimezoneP.addItem("Artic");
		listBoxTimezoneP.addItem("Asia");
		listBoxTimezoneP.addItem("Atlantic");
		listBoxTimezoneP.addItem("Australia");
		listBoxTimezoneP.addItem("Europe");
		listBoxTimezoneP.addItem("Indian");
		listBoxTimezoneP.addItem("Pacific");
		listBoxTimezoneP.setVisibleItemCount(1);
		final ListBox listBoxTimezoneS = new ListBox();
		switchTimezone(listBoxTimezoneS, 0);
		final ListBox listBoxDefaultRouteType = new ListBox();
		listBoxDefaultRouteType.addItem("Tram");
		listBoxDefaultRouteType.addItem("Subway");
		listBoxDefaultRouteType.addItem("Rail");
		listBoxDefaultRouteType.addItem("Bus");
		listBoxDefaultRouteType.addItem("Ferry");
		listBoxDefaultRouteType.addItem("Cable car");
		listBoxDefaultRouteType.addItem("Gondola");
		listBoxDefaultRouteType.addItem("Funicular");
		listBoxDefaultRouteType.setVisibleItemCount(1);
		final Label labelOutdir = new Label(); // Displays wikiuser
		final Label labelResultGz = new Label();    
		final Label labelResult = new Label();    
		final Label labelResultUser = new Label();    

	    /*
	     * Add zip file upload
	     */ 
   	 	final FormPanel uploadFormGz = new FormPanel();
	    uploadFormGz.setAction(GWT.getModuleBaseURL() + "upload");
	    uploadFormGz.setEncoding(FormPanel.ENCODING_MULTIPART); 
	    uploadFormGz.setMethod(FormPanel.METHOD_POST);
	    final VerticalPanel uploadPanelGz = new VerticalPanel();
	    uploadFormGz.setWidget(uploadPanelGz);

	    // Add hidden widget to pass user name to FileUploadServlet for verification against wiki user table
	    final Hidden hwGz = new Hidden("username", wikiUserName);  
	    uploadPanelGz.add(hwGz);

	    // Add hidden widget to pass service start to FileUploadServlet
	    final Hidden ssGz = new Hidden("prefix");  
	    uploadPanelGz.add(ssGz);

	    final FileUpload uploadGz = new FileUpload();
	    uploadGz.setName("uploadFormElement");
	    uploadPanelGz.add(uploadGz);

	    /*
	     * Add Transxchange2GoogleTransit file upload
	     */ 
   	 	final FormPanel uploadForm = new FormPanel();
	    uploadForm.setAction(GWT.getModuleBaseURL() + "upload");
	    uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART); 
	    uploadForm.setMethod(FormPanel.METHOD_POST);
	    final VerticalPanel uploadPanel = new VerticalPanel();
	    uploadForm.setWidget(uploadPanel);

		// Add hidden widget to pass user name to FileUploadServlet for verification against wiki user table
	    final Hidden hw = new Hidden("username", wikiUserName);  
	    uploadPanel.add(hw);

	    final FileUpload upload = new FileUpload();
	    upload.setName("uploadFormElement");
	    uploadPanel.add(upload);
    
		/*
		 * Verify user and enable action buttons if user exists
		 */
		WikiUserServiceAsync wikiUserService = (WikiUserServiceAsync)GWT.create(WikiUserService.class);

		ServiceDefTarget endpoint = (ServiceDefTarget)wikiUserService;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "WikiUser";
		endpoint.setServiceEntryPoint(moduleRelativeURL);    	  

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				Button buttonGz = new Button("Upload", new ClickListener() {
					public void onClick(Widget sender) {
						// Extract service start and pass as prefix
						String serviceStart = textBoxServiceStartGz.getText();
						ssGz.setValue(serviceStart);
						// Upload selected infile to server
						uploadFormGz.submit();
					}
				} );
				Button button = new Button("Transform", new ClickListener() {
					public void onClick(Widget sender) {
						// Upload selected infile to server
						uploadForm.submit();
					}
				} );
				
				if (result != "LOGGED") {
					button.setEnabled(false);
					buttonGz.setEnabled(false);
					labelResultUser.setText((String)result);
				}
				uploadPanel.add(button);
				uploadPanelGz.add(buttonGz);
			}

			public void onFailure(Throwable caught) {
		 		try {
		 			throw caught;
		 		} catch (InvocationException e) {
					labelResult.setText("InvocationException: " + e.getMessage());
		 		} catch (Throwable e) {
					labelResult.setText("callback failed: " + e.getMessage());
		 		}
		 	}
		};
		wikiUserService.wikiUserVerify(wikiUserName, callback);	  
	
		/*
		 * Upload google_transit.zip file
		 */
		uploadFormGz.addFormHandler(new FormHandler() {
			public void onSubmitComplete(FormSubmitCompleteEvent event) {

				labelResultGz.setText((String)event.getResults());
								
			}
		
			public void onSubmit(FormSubmitEvent event) {
				// Upload infile to server
				String inFilename = uploadGz.getFilename();
				if (inFilename.length() == 0) {
			          Window.alert("Infile required");
			          return;
				}
			}
		});
    
		/*
		 * Upload TransXChange file and call Transxchange2GoogleTransit servlet when "transform" button is pushed
		 */
		uploadForm.addFormHandler(new FormHandler() {
			public void onSubmitComplete(FormSubmitCompleteEvent event) {

				labelResult.setText((String)event.getResults());
				
				// Start transformation
				String parseArgs = upload.getFilename();
				parseArgs = parseArgs + " " + textBoxUrl.getText();
				parseArgs = parseArgs + " " + listBoxTimezoneP.getItemText(listBoxTimezoneP.getSelectedIndex());
				String helpString = listBoxTimezoneS.getItemText(listBoxTimezoneS.getSelectedIndex());
				parseArgs = parseArgs + "/" + helpString.substring(0, helpString.indexOf(' '));
				parseArgs = parseArgs + " " + listBoxDefaultRouteType.getSelectedIndex();
				parseArgs = parseArgs + " " + labelOutdir.getText();
			
				// call server through GWT asynchronous RPC
				Transxchange2GoogleTransitServiceAsync transxchange2GoogleTransitService = (Transxchange2GoogleTransitServiceAsync)GWT.create(Transxchange2GoogleTransitService.class);

				ServiceDefTarget endpoint = (ServiceDefTarget)transxchange2GoogleTransitService;
				String moduleRelativeURL = GWT.getModuleBaseURL() + "GTDF";
				endpoint.setServiceEntryPoint(moduleRelativeURL);    	  

				AsyncCallback callback = new AsyncCallback() {
					public void onSuccess(Object result) {
						labelResult.setText((String)result);
					}

					public void onFailure(Throwable caught) {
				 		try {
				 			throw caught;
				 		} catch (InvocationException e) {
							labelResult.setText("InvocationException: " + e.getMessage());
				 		} catch (Throwable e) {
	 						labelResult.setText("callback failed: " + e.getMessage());
				 		}
				 	}
				};
				transxchange2GoogleTransitService.transxchange2GoogleTransit_transform(parseArgs, callback);		
			}
		
			public void onSubmit(FormSubmitEvent event) {
				// Upload infile to server
				String inFilename = upload.getFilename();
				if (inFilename.length() == 0) {
			          Window.alert("Infile required");
			          return;
				}
			}
		});
    
		/*
		 * Add UI elements
		 * 		Better practice (for future reference): use CSS
		 */
	    RootPanel.get("gz_servicestart").add(textBoxServiceStartGz);    
	    RootPanel.get("gz_infile").add(uploadFormGz);    
		RootPanel.get("url").add(textBoxUrl);
		RootPanel.get("timezoneP").add(listBoxTimezoneP);
		RootPanel.get("timezoneS").add(listBoxTimezoneS);
		RootPanel.get("defaultroutetype").add(listBoxDefaultRouteType);
		RootPanel.get("outdir").add(labelOutdir);
		labelOutdir.setText(wikiUserName);
		RootPanel.get("user_result").add(labelResultUser);
		RootPanel.get("gz_result").add(labelResultGz);
		RootPanel.get("result").add(labelResult);
	    RootPanel.get("infile").add(uploadForm);    

	    // Primary time zone (Africa, Europe, ...) selected
		listBoxTimezoneP.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				switchTimezone(listBoxTimezoneS, listBoxTimezoneP.getSelectedIndex());
			}
		});
	}	

	/*
	 * function wrapper for JSNI script to read username from GWT_Transxchange2GoogleTransit.html
	 */
	public static native String readUsername() /*-{
	  return $doc.converter.toastring.value;
	}-*/;
	
	/* 
	 * Switch timezone by  selected continent
	 */	
	private void switchTimezone(ListBox box, int index) {
		switch(index) {
		case 0: // Africa
			box.clear();
			box.addItem("Abidjan - Cote d'Ivoire");
			box.addItem("Accra - Ghana");
			box.addItem("Addis_Ababa - Ethiopia");
			box.addItem("Algiers - Algeria");
			box.addItem("Asmera - Eritrea");
			box.addItem("Bamako - Mali");
			box.addItem("Bangui - Central African Republic");
			box.addItem("Banjul - Gambia");
			box.addItem("Bissau - Guinea-Bissau");
			box.addItem("Blantyre - Malawi");
			box.addItem("Brazzaville - Republic of the Congo");
			box.addItem("Bujumbura - Burundi");
			box.addItem("Cairo - Egypt");
			box.addItem("Casablanca - Morocco");
			box.addItem("Ceuta - Spain, Ceuta & Melilla");
			box.addItem("Conakry - Guinea");
			box.addItem("Dakar - Senegal");
			box.addItem("Dar_es_Salaam - Tanzania");
			box.addItem("Djibouti - Djibouti");
			box.addItem("Douala - Cameroon");
			box.addItem("El_Aaiun - Western Sahara");
			box.addItem("Freetown - Sierra Leone");
			box.addItem("Gaborone - Botswana");
			box.addItem("Harare - Zimbabwe");
			box.addItem("Johannesburg - South Africa");
			box.addItem("Kampala - Uganda");
			box.addItem("Khartoum - Sudan");
			box.addItem("Kigali - Rwanda");
			box.addItem("Kinshasa - Democratic Republic of the Congo, western Democratic Republic of the Congo");
			box.addItem("Lagos - Nigeria");
			box.addItem("Libreville - Gabon");
			box.addItem("Lome - Togo");
			box.addItem("Luanda - Angola");
			box.addItem("Lubumbashi - Democratic Republic of the Congo, eastern Democratic Republic of the Congo");
			box.addItem("Lusaka - Zambia");
			box.addItem("Malabo - Equatorial Guinea");
			box.addItem("Maputo - Mozambique");
			box.addItem("Maseru - Lesotho");
			box.addItem("Mbabane - Swaziland");
			box.addItem("Mogadishu - Somalia");
			box.addItem("Monrovia - Liberia");
			box.addItem("Nairobi - Kenya");
			box.addItem("Ndjamena - Chad");
			box.addItem("Niamey - Niger");
			box.addItem("Nouakchott - Mauritania");
			box.addItem("Ouagadougou - Burkina Faso");
			box.addItem("Porto-Novo - Benin");
			box.addItem("Sao_Tome - Sao Tome & Principe");
			box.addItem("Tripoli - Libya");
			box.addItem("Tunis - Tunisia");
			box.addItem("Windhoek - Namibia");
			box.addItem("");
		break;
		
		case 1: // America
			box.clear();
			box.addItem("Adak - United States, Aleutian Islands");
			box.addItem("Anchorage - United States, Alaska Time");
			box.addItem("Anguilla - Anguilla");
			box.addItem("Antigua - Antigua & Barbuda");
			box.addItem("Araguaina - Brazil, Tocantins");
			box.addItem("Argentina/Buenos_Aires - Argentina, Buenos Aires (BA, CF)");
			box.addItem("Argentina/Catamarca - Argentina, Catamarca (CT), Chubut (CH)");
			box.addItem("Argentina/Cordoba - Argentina, most locations (CB, CC, CN, ER, FM, LP, MN, NQ, RN, SA, SE, SF, SL) ");
			box.addItem("Argentina/Jujuy - Argentina, Jujuy (JY)");
			box.addItem("Argentina/La_Rioja - Argentina, La Rioja (LR)");
			box.addItem("Argentina/Mendoza - Argentina, Mendoza (MZ)");
			box.addItem("Argentina/Rio_Gallegos - Argentina, Santa Cruz (SC)");
			box.addItem("Argentina/San_Juan - Argentina, San Juan (SJ)");
			box.addItem("Argentina/Tucuman - Argentina, Tucuman (TM)");
			box.addItem("Argentina/Ushuaia - Argentina, Tierra del Fuego (TF)");
			box.addItem("Aruba - Aruba");
			box.addItem("Asuncion - Paraguay");
			box.addItem("Atikokan - Canada, Eastern Standard Time - Atikokan, Ontario and Southampton I, Nunavut");
			box.addItem("Bahia - Brazil, Bahia");
			box.addItem("Barbados - Barbados");
			box.addItem("Belem - Brazil, Amapa, E Para");
			box.addItem("Belize - Belize");
			box.addItem("Blanc-Sablon - Canada, Atlantic Standard Time - Quebec - Lower North Shore");
			box.addItem("Boa_Vista - Brazil, Roraima");
			box.addItem("Bogota - Colombia");
			box.addItem("Boise - United States, Mountain Time - south Idaho & east Oregon");
			box.addItem("Cambridge_Bay - Canada, Central Time - west Nunavut");
			box.addItem("Campo_Grande - Brazil, Mato Grosso do Sul");
			box.addItem("Cancun - Mexico, Central Time - Quintana Roo");
			box.addItem("Caracas - Venezuela");
			box.addItem("Cayenne - French Guiana");
			box.addItem("Cayman - Cayman Islands");
			box.addItem("Chicago - United States, Central Time");
			box.addItem("Chihuahua - Mexico, Mountain Time - Chihuahua");
			box.addItem("Costa_Rica - Costa Rica");
			box.addItem("Cuiaba - Brazil, Mato Grosso");
			box.addItem("Curacao - Netherlands Antilles");
			box.addItem("Danmarkshavn - Greenland, east coast, north of Scoresbysund");
			box.addItem("Dawson - Canada, Pacific Time - north Yukon");
			box.addItem("Dawson_Creek - Canada, Mountain Standard Time - Dawson Creek & Fort Saint John, British Columbia");
			box.addItem("Denver - United States, Mountain Time");
			box.addItem("Detroit - United States, Eastern Time - Michigan - most locations");
			box.addItem("Dominica - Dominica");
			box.addItem("Edmonton - Canada, Mountain Time - Alberta, east British Columbia & west Saskatchewan");
			box.addItem("Eirunepe - Brazil, W Amazonas");
			box.addItem("El_Salvador - El Salvado");
			box.addItem("Fortaleza - Brazil, NE Brazil (MA, PI, CE, RN, PB)");
			box.addItem("Glace_Bay - Canada, Atlantic Time - Nova Scotia - places that did not observe DST 1966-1971");
			box.addItem("Godthab - Greenland, most locations");
			box.addItem("Goose_Bay - Canada, Atlantic Time - E Labrador");
			box.addItem("Grand_Turk - Turks and Caicos Islands");
			box.addItem("Grenada - Grenada");
			box.addItem("Guadeloupe - Guadeloupe");
			box.addItem("Guatemala - Guatemala");
			box.addItem("Guayaquil - Ecuador, mainland");
			box.addItem("Guyana - Guyana");
			box.addItem("Halifax - Canada, Atlantic Time - Nova Scotia (most places), W Labrador, E Quebec & PEI");
			box.addItem("Havana - Cuba");
			box.addItem("Hermosillo - Mexico, Mountain Standard Time - Sonora");
			box.addItem("Indiana/Indianapolis - United States, Eastern Time - Indiana - most locations");
			box.addItem("Indiana/Knox - United States, Eastern Time - Indiana - Starke County");
			box.addItem("Indiana/Marengo - United States, Eastern Time - Indiana - Crawford County");
			box.addItem("Indiana/Petersburg - United States, Central Time - Indiana - Pike County");
			box.addItem("Indiana/Vevay - United States, Eastern Time - Indiana - Switzerland County");
			box.addItem("Indiana/Vincennes - United States, Central Time - Indiana - Daviess, Dubois, Knox, Martin & Perry Counties");
			box.addItem("Indiana/Winamac - United States, Eastern Time - Indiana - Pulaski County");
			box.addItem("Inuvik - Canada, Mountain Time - west Northwest Territories");
			box.addItem("Iqaluit - Canada, Eastern Time - east Nunavut");
			box.addItem("Jamaica - Jamaica");
			box.addItem("Juneau - United States, Alaska Time - Alaska panhandle");
			box.addItem("Kentucky/Louisville - United States, Eastern Time - Kentucky - Louisville area");
			box.addItem("Kentucky/Monticello - United States, Eastern Time - Kentucky - Wayne County");
			box.addItem("La_Paz - Bolivia");
			box.addItem("Lima - Peru");
			box.addItem("Los_Angeles - United States, Pacific Time");
			box.addItem("Maceio - Brazil, Alagoas, Sergipe");
			box.addItem("Managua - Nicaragua");
			box.addItem("Manaus - Brazil, E Amazonas");
			box.addItem("Martinique - Martinique");
			box.addItem("Mazatlan - Mexico, Mountain Time - S Baja, Nayarit, Sinaloa");
			box.addItem("Menominee - United States, Central Time - Michigan - Dickinson, Gogebic, Iron & Menominee Counties");
			box.addItem("Merida - Mexico, Central Time - Campeche, Yucatan");
			box.addItem("Mexico_City - Mexico, Central Time - most locations");
			box.addItem("Miquelon - Saint-Pierre and Miquelon");
			box.addItem("Moncton - Canada, Atlantic Time - New Brunswick");
			box.addItem("Monterrey - Mexico, Central Time - Coahuila, Durango, Nuevo Leon, Tamaulipas");
			box.addItem("Montevideo - Uruguay");
			box.addItem("Montreal - Canada, Eastern Time - Quebec - most locations");
			box.addItem("Montserrat - Montserrat");
			box.addItem("Nassau - Bahamas");
			box.addItem("New_York - United States, Eastern Time");
			box.addItem("Nipigon - Canada, Eastern Time - Ontario & Quebec - places that did not observe DST 1967-1973");
			box.addItem("Nome - United States, Alaska Time - west Alaska");
			box.addItem("Noronha - Brazil, Atlantic islands");
			box.addItem("North_Dakota/Center - United States, Central Time - North Dakota - Oliver County");
			box.addItem("North_Dakota/New_Salem - United States, Mountain Time - North Dakota - Morton County (except Mandan area)");
			box.addItem("Panama - Panama");
			box.addItem("Pangnirtung - Canada, Eastern Time - Pangnirtung, Nunavut");
			box.addItem("Paramaribo - Suriname");
			box.addItem("Phoenix - United States, Mountain Standard Time - Arizona");
			box.addItem("Port-au-Prince - Haiti");
			box.addItem("Port_of_Spain - Trinidad & Tobago");
			box.addItem("Porto_Velho - Brazil, W Para, Rondonia");
			box.addItem("Puerto_Rico - Puerto Rico");
			box.addItem("Rainy_River - Canada, Central Time - Rainy River & Fort Frances, Ontario");
			box.addItem("Rankin_Inlet - Canada, Central Time - central Nunavut");
			box.addItem("Recife - Brazil, Pernambuco");
			box.addItem("Regina - Canada, Central Standard Time - Saskatchewan - most locations");
			box.addItem("Rio_Branco - Brazil, Acre");
			box.addItem("Santiago - Chile, most locations");
			box.addItem("Santo_Domingo - Dominican Republic");
			box.addItem("Sao_Paulo - Brazil, S & SE Brazil (GO, DF, MG, ES, RJ, SP, PR, SC, RS)");
			box.addItem("Scoresbysund - Greenland, Scoresbysund / Ittoqqortoormiit");
			box.addItem("Shiprock - United States, Mountain Time - Navajo");
			box.addItem("St_Johns - Canada, Newfoundland Islan");
			box.addItem("St_Kitts - St Kitts & Nevis");
			box.addItem("St_Lucia - St Lucia");
			box.addItem("St_Thomas - Virgin Islands (US)");
			box.addItem("St_Vincent - Saint Vincent and the Grenadines");
			box.addItem("Swift_Current - Canada, Central Standard Time - Saskatchewan - midwest");
			box.addItem("Tegucigalpa - Honduras");
			box.addItem("Thule - Greenland, Thule / Pituffik");
			box.addItem("Thunder_Bay - Canada, Eastern Time - Thunder Bay, Ontario");
			box.addItem("Tijuana - Mexico, Pacific Time");
			box.addItem("Toronto - Canada, Eastern Time - Ontario - most locations");
			box.addItem("Tortola - Virgin Islands (UK)");
			box.addItem("Vancouver - Canada, Pacific Time - west British Columbia");
			box.addItem("Whitehorse - Canada, Pacific Time - south Yukon");
			box.addItem("Winnipeg - Canada, Central Time - Manitoba & west Ontario");
			box.addItem("Yakutat - United States, Alaska Time - Alaska panhandle neck");
			box.addItem("Yellowknife - Canada, Mountain Time - central Northwest Territories");
		break;
		case 2: // Antarctica
			box.clear();
			box.addItem("Casey - Antarctica, Casey Station, Bailey Peninsula");
			box.addItem("Davis - Antarctica, Davis Station, Vestfold Hills");
			box.addItem("DumontDUrville - Antarctica, Dumont-d'Urville Base, Terre Adelie");
			box.addItem("Mawson - Antarctica, Mawson Station, Holme Bay");
			box.addItem("McMurdo - Antarctica, McMurdo Station, Ross Island");
			box.addItem("Palmer - Antarctica, Palmer Station, Anvers Island");
			box.addItem("Rothera - Antarctica, Rothera Station, Adelaide Island");
			box.addItem("South_Pole - Antarctica, Amundsen-Scott Station, South Pole");
			box.addItem("Syowa - Antarctica, Syowa Station, E Ongul I");
			box.addItem("Vostok - Antarctica, Vostok Station, S Magnetic Pole");
			break;
		case 3: // Artic
			box.clear();
			box.addItem("Longyearbyen - Svalbard");
		break;
		case 4: // Asia
			box.clear();
			box.addItem("Aden - Yemen");
			box.addItem("Almaty - Kazakhstan, most locations");
			box.addItem("Amman - Jordan");
			box.addItem("Anadyr - Russia, Moscow+10 - Bering Sea");
			box.addItem("Aqtau - Kazakhstan, Atyrau (Atirau, Gur'yev), Mangghystau (Mankistau)");
			box.addItem("Aqtobe - Kazakhstan, Aqtobe (Aktobe)");
			box.addItem("Ashgabat - Turkmenistan");
			box.addItem("Baghdad - Iraq");
			box.addItem("Bahrain - Bahrain");
			box.addItem("Baku - Azerbaijan");
			box.addItem("Bangkok - Thailand");
			box.addItem("Beirut - Lebanon");
			box.addItem("Bishkek - Kyrgyzstan");
			box.addItem("Brunei - Brunei");
			box.addItem("Calcutta - India");
			box.addItem("Choibalsan - Mongolia, Dornod, Sukhbaatar");
			box.addItem("Chongqing - China, central China - Gansu, Guizhou, Sichuan, Yunnan, etc.");
			box.addItem("Colombo - Sri Lanka");
			box.addItem("Damascus - Syria");
			box.addItem("Dhaka - Bangladesh");
			box.addItem("Dili - East Timor");
			box.addItem("Dubai - United Arab Emirates");
			box.addItem("Dushanbe - Tajikistan");
			box.addItem("Gaza - Palestinian Authority");
			box.addItem("Harbin - China, Heilongjiang");
			box.addItem("Hong_Kong - Hong Kong");
			box.addItem("Hovd - Mongolia, Bayan-Olgiy, Govi-Altai, Hovd, Uvs, Zavkhan");
			box.addItem("Irkutsk - Russia, Moscow+05 - Lake Baikal");
			box.addItem("Jakarta - Indonesia, Java & Sumatra");
			box.addItem("Jayapura - Indonesia, Irian Jaya & the Moluccas");
			box.addItem("Jerusalem - Israel");
			box.addItem("Kabul - Afghanistan");
			box.addItem("Kamchatka - Russia, Moscow+09 - Kamchatka");
			box.addItem("Karachi - Pakistan");
			box.addItem("Kashgar - China, southwest Xinjiang Uyghur");
			box.addItem("Katmandu - Nepal");
			box.addItem("Krasnoyarsk - Russia, Moscow+04 - Yenisei River");
			box.addItem("Kuala_Lumpur - Malaysia, peninsular Malaysia");
			box.addItem("Kuching - Malaysia, Sabah & Sarawak");
			box.addItem("Kuwait - Kuwait");
			box.addItem("Macau - Macau");
			box.addItem("Magadan - Russia, Moscow+08 - Magadan");
			box.addItem("Makassar - Indonesia, east & south Borneo, Celebes, Bali, Nusa Tengarra, west Timor");
			box.addItem("Manila - Philippines");
			box.addItem("Muscat - Oman");
			box.addItem("Nicosia - Cyprus");
			box.addItem("Novosibirsk - Russia, Moscow+03 - Novosibirsk");
			box.addItem("Omsk - Russia, Moscow+03 - west Siberia");
			box.addItem("Oral - Kazakhstan, West Kazakhstan");
			box.addItem("Phnom_Penh - Cambodia");
			box.addItem("Pontianak - Indonesia, west & central Borneo");
			box.addItem("Pyongyang - Korea (North)");
			box.addItem("Qatar - Qatar");
			box.addItem("Qyzylorda - Kazakhstan, Qyzylorda (Kyzylorda, Kzyl-Orda)");
			box.addItem("Rangoon - Myanmar (Burma)");
			box.addItem("Riyadh - Saudi Arabia");
			box.addItem("Saigon - Vietnam");
			box.addItem("Sakhalin - Russia, Moscow+07 - Sakhalin Island ");
			box.addItem("Samarkand - Uzbekistan, west Uzbekistan");
			box.addItem("Seoul - Korea (South)");
			box.addItem("Shanghai - China, east China - Beijing, Guangdong, Shanghai, etc.");
			box.addItem("Singapore - Singapore");
			box.addItem("Taipei - Taiwan");
			box.addItem("Tashkent - Uzbekistan, east Uzbekistan");
			box.addItem("Tbilisi - Georgia");
			box.addItem("Tehran - Iran");
			box.addItem("Thimphu - Bhutan");
			box.addItem("Tokyo - Japan");
			box.addItem("Ulaanbaatar - Mongolia, most locations");
			box.addItem("Urumqi - China, Tibet & most of Xinjiang Uyghur");
			box.addItem("Vientiane - Laos");
			box.addItem("Vladivostok - Russia, Moscow+07 - Amur River");
			box.addItem("Yakutsk - Russia, Moscow+06 - Lena River");
			box.addItem("Yekaterinburg - Russia, Moscow+02 - Urals");
			box.addItem("Yerevan - Armenia");
		break;
		case 5: // Atlantic
			box.clear();
			box.addItem("Azores - Portugal, Azores");
			box.addItem("Bermuda - Bermuda");
			box.addItem("Canary - Spain, Canary Islands");
			box.addItem("Cape_Verde - Cape Verde");
			box.addItem("Faroe - Faroe Islands");
			box.addItem("Jan_Mayen - Jan Mayen");
			box.addItem("Madeira - Portugal, Madeira Islands");
			box.addItem("Reykjavik - Iceland");
			box.addItem("South_Georgia - South Georgia and the South Sandwich Islands");
			box.addItem("St_Helena - St Helena");
			box.addItem("Stanley - Falkland Islands");
		break;
		case 6: // Australia
			box.clear();
			box.addItem("Adelaide - Australia, South Australia");
			box.addItem("Brisbane - Australia, Queensland - most locations");
			box.addItem("Broken_Hill - Australia, New South Wales - Yancowinna");
			box.addItem("Currie - Australia, Tasmania - King Island");
			box.addItem("Darwin - Australia, Northern Territory");
			box.addItem("Eucla - Australia, Western Australia - Eucla area");
			box.addItem("Hobart - Australia, Tasmania - most locations");
			box.addItem("Lindeman - Australia, Queensland - Holiday Islands");
			box.addItem("Lord_Howe - Australia, Lord Howe Island");
			box.addItem("Melbourne - Australia, Victoria");
			box.addItem("Perth - Australia, Western Australia - most locations");
			box.addItem("Sydney - Australia, New South Wales - most locations");
		break;
		case 7: // Europe
			box.clear();
			box.addItem("Amsterdam - Netherlands");
			box.addItem("Andorra - Andorra");
			box.addItem("Athens - Greece");
			box.addItem("Belgrade - Serbia");
			box.addItem("Berlin - Germany");
			box.addItem("Bratislava - Slovaki");
			box.addItem("Brussels - Belgium");
			box.addItem("Bucharest - Romania");
			box.addItem("Budapest - Hungary");
			box.addItem("Chisinau - Moldova");
			box.addItem("Copenhagen - Denmark");
			box.addItem("Dublin - Ireland");
			box.addItem("Gibraltar - Gibraltar");
			box.addItem("Guernsey - Guernsey");
			box.addItem("Helsinki - Finland");
			box.addItem("Isle_of_Man - Isle of Man");
			box.addItem("Istanbul - Turkey");
			box.addItem("Jersey - Jersey");
			box.addItem("Kaliningrad - Russia, Moscow-01 - Kaliningrad");
			box.addItem("Kiev - Ukraine, most locations");
			box.addItem("Lisbon - Portugal, mainland");
			box.addItem("Ljubljana - Slovenia");
			box.addItem("London - United Kingdom");
			box.addItem("Luxembourg - Luxembourg");
			box.addItem("Madrid - Spain, mainland");
			box.addItem("Malta - Malta");
			box.addItem("Mariehamn - Aaland Islands");
			box.addItem("Minsk - Belarus");
			box.addItem("Monaco - Monaco");
			box.addItem("Moscow - Russia, Moscow+00 - west Russia");
			box.addItem("Oslo - Norway");
			box.addItem("Paris - France");
			box.addItem("Podgorica - Montenegro");
			box.addItem("Prague - Czech Republic");
			box.addItem("Riga - Latvia");
			box.addItem("Rome - Italy");
			box.addItem("Samara - Russia, Moscow+01 - Caspian Sea");
			box.addItem("San_Marino - San Marino");
			box.addItem("Sarajevo - Bosnia & Herzegovina");
			box.addItem("Simferopol - Ukraine, central Crimea");
			box.addItem("Skopje - Republic of Macedonia");
			box.addItem("Sofia - Bulgaria");
			box.addItem("Stockholm - Sweden");
			box.addItem("Tallinn - Estonia");
			box.addItem("Tirane - Albania");
			box.addItem("Uzhgorod - Ukraine, Ruthenia");
			box.addItem("Vaduz - Liechtenstein");
			box.addItem("Vatican - Vatican City");
			box.addItem("Vienna - Austria");
			box.addItem("Vilnius - Lithuania");
			box.addItem("Volgograd - Russia, Moscow+00 - Caspian Sea");
			box.addItem("Warsaw - Poland");
			box.addItem("Zagreb - Croatia");
			box.addItem("Zaporozhye - Ukraine, Zaporozh'ye, E Lugans");
			box.addItem("Zurich - Switzerland");
		break;
		case 8: // Indian
			box.clear();
			box.addItem("Antananarivo - Madagascar");
			box.addItem("Chagos - British Indian Ocean Territory");
			box.addItem("Christmas - Christmas Island");
			box.addItem("Cocos - Cocos (Keeling) Islands");
			box.addItem("Comoro - Comoros");
			box.addItem("Kerguelen - French Southern & Antarctic Lands");
			box.addItem("Mahe - Seychelles");
			box.addItem("Maldives - Maldives");
			box.addItem("Mauritius - Mauritius");
			box.addItem("Mayotte - Mayotte");
			box.addItem("Reunion - Reunion");
		break;
		case 9: // Pacific
			box.clear();
			box.addItem("Apia - Samoa (western)");
			box.addItem("Auckland - New Zealand, most locations");
			box.addItem("Chatham - New Zealand, Chatham Islands");
			box.addItem("Easter - Chile, Easter Island & Sala y Gomez");
			box.addItem("Efate - Vanuatu");
			box.addItem("Enderbury - Kiribati, Phoenix Islands");
			box.addItem("Fakaofo - Tokelau");
			box.addItem("Fiji - Fiji");
			box.addItem("Funafuti - Tuvalu");
			box.addItem("Galapagos - Ecuador, Galapagos Islands");
			box.addItem("Gambier - French Polynesia, Gambier Islands");
			box.addItem("Guadalcanal - Solomon Islands");
			box.addItem("Guam - Guam");
			box.addItem("Honolulu - United States, Hawaii");
			box.addItem("Johnston - US minor outlying islands, Johnston Atoll");
			box.addItem("Kiritimati - Kiribati, Line Islands");
			box.addItem("Kosrae - Micronesia, Kosrae");
			box.addItem("Kwajalein - Marshall Islands, Kwajalein");
			box.addItem("Majuro - Marshall Islands, most locations");
			box.addItem("Marquesas - French Polynesia, Marquesas Islands");
			box.addItem("Midway - US minor outlying islands, Midway Islands");
			box.addItem("Nauru - Nauru");
			box.addItem("Niue - Niue");
			box.addItem("Norfolk - Norfolk Island");
			box.addItem("Noumea - New Caledonia");
			box.addItem("Pago_Pago - Samoa (American)");
			box.addItem("Palau - Palau");
			box.addItem("Pitcairn - Pitcairn");
			box.addItem("Ponape - Micronesia, Ponape (Pohnpei)");
			box.addItem("Port_Moresby - Papua New Guinea");
			box.addItem("Rarotonga - Cook Islands");
			box.addItem("Saipan - Northern Mariana Islands");
			box.addItem("Tahiti - French Polynesia, Society Islands");
			box.addItem("Tarawa - Kiribati, Gilbert Islands");
			box.addItem("Tongatapu - Tonga");
			box.addItem("Truk - Micronesia, Truk (Chuuk) and Yap");
			box.addItem("Wake - US minor outlying islands, Wake Island");
			box.addItem("Wallis - Wallis & Futuna");
		break;
		default: ;
		break;
		}
		
	}
}




