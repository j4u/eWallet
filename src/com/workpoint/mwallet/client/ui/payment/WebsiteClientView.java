package com.workpoint.mwallet.client.ui.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.workpoint.mwallet.client.ui.component.DropDownList;
import com.workpoint.mwallet.client.ui.component.TextField;

public class WebsiteClientView extends ViewImpl implements
		WebsiteClientPresenter.MyView {

	private final Widget widget;

	@UiField
	Frame framePayment;

	@UiField
	Anchor aNext;
	@UiField
	Anchor aBack;

	@UiField
	DivElement divPackage;
	@UiField
	DivElement divPayment;
	@UiField
	DivElement divComplete;

	@UiField
	LIElement liPackage;
	@UiField
	LIElement liPayment;
	@UiField
	LIElement liComplete;

	@UiField
	TextField surname;
	@UiField
	TextField other_names;
	@UiField
	TextField email_address;
	
	// @UiField
	// DropDownList<> lstMemberCategory;

	@UiField
	TextField employer;
	@UiField
	TextField city;
	@UiField
	TextField address;
	@UiField
	TextField postal_code;

	private List<LIElement> liElements = new ArrayList<LIElement>();
	private List<DivElement> divElements = new ArrayList<DivElement>();

	int counter = 0;

	public interface Binder extends UiBinder<Widget, WebsiteClientView> {
	}

	@Inject
	public WebsiteClientView(final Binder binder) {
		widget = binder.createAndBindUi(this);

		String url = "http://197.248.2.44:8080/ewallet-beta/#websiteClient";
		framePayment.setUrl(url);
		
		// Li Elements
		liElements.add(liPackage);
		liElements.add(liPayment);
		liElements.add(liComplete);

		// Div Elements
		divElements.add(divPackage);
		divElements.add(divPayment);
		divElements.add(divComplete);

		setActive(liElements.get(counter), divElements.get(counter));

		aBack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				counter = counter-1;
				removeActive(liElements.get(counter), divElements.get(counter));
				setActive(liElements.get(counter), divElements.get(counter));
			}
		});

		aNext.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				counter = counter+1;
				setActive(liElements.get(counter), divElements.get(counter));
			}
		});
	}

	private void removeActive(LIElement liElement, DivElement divElement) {
		liElement.removeClassName("active");
		divElement.removeClassName("active");
		System.err.println("Removed:" + counter);
	}

	private void setActive(LIElement liElement, DivElement divElement) {
		if(counter==0){
			aBack.setVisible(false);
		}
		clearAll();
		liElement.addClassName("active");
		divElement.addClassName("active");
		System.err.println("Added:" + counter);
	}

	private void clearAll() {
		liPackage.removeClassName("active");
		liPayment.removeClassName("active");
		liComplete.removeClassName("active");

		divPackage.removeClassName("active");
		divPayment.removeClassName("active");
		divComplete.removeClassName("active");
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void createWizard() {

	}
}
