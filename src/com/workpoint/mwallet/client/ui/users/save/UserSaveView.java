package com.workpoint.mwallet.client.ui.users.save;

import java.util.List;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.workpoint.mwallet.client.model.UploadContext;
import com.workpoint.mwallet.client.model.UploadContext.UPLOADACTION;
import com.workpoint.mwallet.client.ui.component.ActionLink;
import com.workpoint.mwallet.client.ui.component.DropDownList;
import com.workpoint.mwallet.client.ui.component.IssuesPanel;
import com.workpoint.mwallet.client.ui.component.PasswordField;
import com.workpoint.mwallet.client.ui.component.TextArea;
import com.workpoint.mwallet.client.ui.component.TextField;
import com.workpoint.mwallet.client.ui.component.autocomplete.AutoCompleteField;
import com.workpoint.mwallet.client.ui.upload.custom.Uploader;
import com.workpoint.mwallet.client.ui.users.save.UserSavePresenter.TYPE;
import com.workpoint.mwallet.shared.model.CategoryDTO;
import com.workpoint.mwallet.shared.model.UserDTO;
import com.workpoint.mwallet.shared.model.UserGroup;

public class UserSaveView extends PopupViewImpl implements
		UserSavePresenter.IUserSaveView {

	private final Widget widget;
	@UiField
	HTMLPanel divUserDetails;
	@UiField
	HTMLPanel divGroupDetails;
	@UiField
	HTMLPanel panelHeader;

	@UiField
	IssuesPanel issues;
	@UiField
	Anchor aClose;
	@UiField
	ActionLink aPickUser;

	TabPanel t;

	@UiField
	TextField txtUserName;
	@UiField
	TextField txtFirstname;
	@UiField
	TextField txtLastname;
	@UiField
	TextField txtEmail;
	@UiField
	TextField txtPhone;
	@UiField
	PasswordField txtPassword;
	@UiField
	PasswordField txtConfirmPassword;

	@UiField
	TextField txtGroupname;
	@UiField
	TextArea txtDescription;
	@UiField
	SpanElement spnMessage;
	@UiField
	TextField txtSearchBox;

	@UiField
	PopupPanel AddUserDialog;
	@UiField
	Anchor aSaveGroup;
	@UiField
	Anchor aSaveUser;

	@UiField
	SpanElement header;

	@UiField
	HTMLPanel divUserSave;
	@UiField
	Uploader uploader;
	@UiField
	AutoCompleteField<UserGroup> lstGroups;
	@UiField
	DropDownList<CategoryDTO> lstCategoryType;

	TYPE type;

	public interface Binder extends UiBinder<Widget, UserSaveView> {
	}

	@Inject
	public UserSaveView(final EventBus eventBus, final Binder binder) {
		super(eventBus);
		widget = binder.createAndBindUi(this);
		aClose.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		// ----Calculate the Size of Screen; To be Centralized later -----
		int height = Window.getClientHeight();
		int width = Window.getClientWidth();

		/* Percentage to the Height and Width */
		double height1 = (5.0 / 100.0) * height;
		double width1 = (50.0 / 100.0) * width;

		AddUserDialog.setPopupPosition((int) width1, (int) height1);

		txtUserName.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setContext(event.getValue());
			}
		});

	}

	protected void setContext(String value) {
		UploadContext context = new UploadContext();
		context.setAction(UPLOADACTION.UPLOADUSERIMAGE);
		context.setContext("userId", value + "");
		context.setAccept("png,jpeg,jpg,gif");
		uploader.setContext(context);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	public boolean isValid() {

		boolean isValid = true;

		switch (type) {
		case GROUP:
			isValid = isGroupValid();
			break;

		default:
			isValid = isUserValid();
			break;
		}

		return isValid;
	}

	public UserGroup getGroup() {
		UserGroup group = new UserGroup();
		group.setFullName(txtDescription.getValue());
		group.setName(txtGroupname.getValue());

		return group;
	}

	public void setGroup(UserGroup group) {
		txtDescription.setValue(group.getFullName());
		txtGroupname.setValue(group.getName());
	}

	public UserDTO getUser() {
		UserDTO user = new UserDTO();
		user.setEmail(txtEmail.getValue());
		user.setFirstName(txtFirstname.getValue());
		if (!isNullOrEmpty(txtPassword.getValue())) {
			user.setPassword(txtPassword.getValue());
		}
		user.setPhoneNo(txtPhone.getValue());
		user.setLastName(txtLastname.getValue());
		user.setUserId(txtUserName.getValue());
		user.setGroups(lstGroups.getSelectedItems());
		user.setCategory(lstCategoryType.getValue());
		return user;
	}

	UserDTO user;

	public void setUser(UserDTO user) {
		// //System.err.println("Called setUser");

		txtEmail.setValue(user.getEmail());
		txtFirstname.setValue(user.getFirstName());
		txtLastname.setValue(user.getLastName());
		txtUserName.setValue(user.getUserId());
		txtPassword.setValue(user.getPassword());
		txtPhone.setValue(user.getPhoneNo());
		txtConfirmPassword.setValue(user.getPassword());
		txtUserName.setDisabled(true);
		lstGroups.select(user.getGroups());
		lstCategoryType.setValue(user.getCategory());
		setContext(user.getUserId());

	}

	private boolean isUserValid() {
		issues.clear();
		boolean valid = true;

		if (isNullOrEmpty(txtFirstname.getValue())) {
			valid = false;
			issues.addError("First Name is mandatory");
		}

		if (isNullOrEmpty(txtLastname.getValue())) {
			valid = false;
			issues.addError("First Name is mandatory");
		}

		if (isNullOrEmpty(txtEmail.getValue())) {
			// valid = false;
			// issues.addError("Email is mandatory");
		}

		if (isNullOrEmpty(txtPassword.getText())) {
			// valid=false;
			// issues.addError("Password is mandatory");
		} else {
			if (!txtPassword.getValue().equals(txtConfirmPassword.getValue())) {
				issues.addError("Password and confirm password fields do not match");
			}
		}

		if (lstGroups.getSelectedItems().size() == 0) {
			valid = false;
			issues.addError("User must belong to a Group");
		}
		return valid;
	}

	private boolean isGroupValid() {

		issues.clear();
		boolean valid = true;

		if (isNullOrEmpty(txtGroupname.getValue())) {
			valid = false;
			issues.addError("Group Name is mandatory");
		}

		return valid;
	}

	boolean isNullOrEmpty(String value) {
		return value == null || value.trim().length() == 0;
	}

	public HasClickHandlers getSaveUser() {
		return aSaveUser;
	}

	public HasClickHandlers getPickUser() {
		return aPickUser;
	}

	public HasClickHandlers getSaveGroup() {
		return aSaveGroup;
	}

	public HasKeyDownHandlers getSearchBox() {
		return txtSearchBox;
	}

	@Override
	public void setType(TYPE type) {
		this.type = type;
		if (type == TYPE.GROUP) {
			divGroupDetails.removeStyleName("hide");
			divUserDetails.addStyleName("hide");
			divUserSave.addStyleName("hide");
			header.setInnerText("New Group");
		} else {
			divUserDetails.removeStyleName("hide");
			divGroupDetails.addStyleName("hide");
			divUserSave.removeStyleName("hide");
			header.setInnerText("New User");
		}
	}

	@Override
	public void setGroups(List<UserGroup> groups) {
		lstGroups.addItems(groups);
	}

	@Override
	public void setMessage(String message, String styleName) {
		spnMessage.setInnerText(message);
		spnMessage.setClassName(styleName);
	}

	public String getClientCode() {
		if (isNullOrEmpty(txtSearchBox.getText())) {
			setMessage("Please Enter a valid Linking code", "text-error");
			return null;
		} else {
			return txtSearchBox.getText();
		}

	}

	@Override
	public void setToWidget(boolean isWidget) {
		if (isWidget) {
			panelHeader.setVisible(false);
			divUserSave.setVisible(false);
		} else {
			panelHeader.setVisible(true);
			divUserSave.setVisible(true);
		}
	}

	@Override
	public void setCategories(List<CategoryDTO> categories) {
		// //System.err.println("Loaded Categories");
		lstCategoryType.setItems(categories);
	}

}
