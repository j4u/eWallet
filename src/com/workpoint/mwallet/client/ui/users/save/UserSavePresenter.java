package com.workpoint.mwallet.client.ui.users.save;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.workpoint.mwallet.client.service.TaskServiceCallback;
import com.workpoint.mwallet.client.ui.events.ActivitySavedEvent;
import com.workpoint.mwallet.client.ui.events.LoadGroupsEvent;
import com.workpoint.mwallet.client.ui.events.LoadUsersEvent;
import com.workpoint.mwallet.client.ui.events.ProcessingCompletedEvent;
import com.workpoint.mwallet.client.ui.events.ProcessingEvent;
import com.workpoint.mwallet.shared.model.CategoryDTO;
import com.workpoint.mwallet.shared.model.ClientDTO;
import com.workpoint.mwallet.shared.model.UserDTO;
import com.workpoint.mwallet.shared.model.UserGroup;
import com.workpoint.mwallet.shared.requests.GetCategoriesRequest;
import com.workpoint.mwallet.shared.requests.GetGroupsRequest;
import com.workpoint.mwallet.shared.requests.ImportClientRequest;
import com.workpoint.mwallet.shared.requests.MultiRequestAction;
import com.workpoint.mwallet.shared.requests.SaveGroupRequest;
import com.workpoint.mwallet.shared.requests.SaveUserRequest;
import com.workpoint.mwallet.shared.responses.GetCategoriesRequestResult;
import com.workpoint.mwallet.shared.responses.GetGroupsResponse;
import com.workpoint.mwallet.shared.responses.ImportClientResponse;
import com.workpoint.mwallet.shared.responses.MultiRequestActionResult;
import com.workpoint.mwallet.shared.responses.SaveGroupResponse;
import com.workpoint.mwallet.shared.responses.SaveUserResponse;

public class UserSavePresenter extends
		PresenterWidget<UserSavePresenter.IUserSaveView> {

	public interface IUserSaveView extends PopupView {

		void setType(TYPE type);

		HasClickHandlers getSaveUser();

		HasClickHandlers getSaveGroup();

		boolean isValid();

		UserDTO getUser();

		void setUser(UserDTO user);

		UserGroup getGroup();

		void setGroup(UserGroup group);

		void setGroups(List<UserGroup> groups);

		HasClickHandlers getPickUser();

		void setMessage(String message, String styleName);

		String getClientCode();

		HasKeyDownHandlers getSearchBox();

		void setToWidget(boolean isWidget);

		void setCategories(List<CategoryDTO> categories);

	}

	public enum TYPE {
		GROUP, USER
	}

	TYPE type;

	UserDTO user;

	UserGroup group;

	@Inject
	DispatchAsync requestHelper;

	@Inject
	public UserSavePresenter(final EventBus eventBus, final IUserSaveView view) {
		super(eventBus, view);
	}

	@Override
	protected void onBind() {
		super.onBind();

		getView().getSaveUser().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getView().isValid()) {
					UserDTO htuser = getView().getUser();
					if (user != null) {
						htuser.setId(user.getId());
					}
					SaveUserRequest request = new SaveUserRequest(htuser);
					requestHelper.execute(request,
							new TaskServiceCallback<SaveUserResponse>() {
								@Override
								public void processResult(
										SaveUserResponse result) {
									user = result.getUser();
									getView().setUser(user);
									getView().hide();
									fireEvent(new LoadUsersEvent(user));
									fireEvent(new ActivitySavedEvent(
											"successfully saved User "
													+ user.getDisplayName()));
								}
							});
				}
			}
		});

		getView().getSaveGroup().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveUserGroup();
			}
		});

		getView().getPickUser().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchUser();
			}
		});

		getView().getSearchBox().addKeyDownHandler(keyHandler);
	}

	public void saveUserGroup() {
		if (getView().isValid()) {
			UserGroup userGroup = getView().getGroup();

			SaveGroupRequest request = new SaveGroupRequest(userGroup);

			requestHelper.execute(request,
					new TaskServiceCallback<SaveGroupResponse>() {
						@Override
						public void processResult(SaveGroupResponse result) {
							group = result.getGroup();
							getView().setGroup(group);

							fireEvent(new LoadGroupsEvent());
							fireEvent(new ActivitySavedEvent("Successfully saved group "+group.getDisplayName()));
							getView().hide();
						}
					});
		}
	}

	KeyDownHandler keyHandler = new KeyDownHandler() {
		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				searchUser();
			}
		}
	};

	public void searchUser() {
		String clCode = getView().getClientCode();
		if (clCode == null) {
			return;
		}
		fireEvent(new ProcessingEvent());
		requestHelper.execute(new ImportClientRequest(clCode),
				new TaskServiceCallback<ImportClientResponse>() {
					@Override
					public void processResult(ImportClientResponse aResponse) {
						fireEvent(new ProcessingCompletedEvent());
						ClientDTO client = aResponse.getClient();
						if (client == null) {
							getView().setMessage("No Client Record found.",
									"text-error");
							return;
						}
						getView().setMessage("", "hide");
						UserDTO user = new UserDTO();
						user.setFirstName(client.getFirstName());
						user.setLastName(client.getMiddleName().trim() + " "
								+ client.getSirName().trim());
						user.setPhoneNo(client.getPhoneNo());
						user.setUserId(client.getClCode());
						user.setPassword(client.getPhoneNo());
						setType(TYPE.USER, user);
					}

				});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setToWidget(false);
	}

	@Override
	protected void onReset() {
		super.onReset();
	}

	public void setToWidget(boolean isWidget) {
		getView().setToWidget(isWidget);
	}

	public void loadData() {
		MultiRequestAction action = new MultiRequestAction();
		action.addRequest(new GetGroupsRequest());
		action.addRequest(new GetCategoriesRequest());

		requestHelper.execute(action,
				new TaskServiceCallback<MultiRequestActionResult>() {
					@Override
					public void processResult(MultiRequestActionResult result) {
						int i = 0;
						// Groups
						GetGroupsResponse gResponse = (GetGroupsResponse) result
								.get(i++);
						getView().setGroups(gResponse.getGroups());

						// Categories Response
						GetCategoriesRequestResult cResponse = (GetCategoriesRequestResult) result
								.get(i++);
						List<CategoryDTO> categories = cResponse
								.getCategories();
						getView().setCategories(categories);

						getView().setUser(user);

					}
				});
	}

	public void setType(TYPE type, Object value) {
		this.type = type;
		getView().setType(type);
		if (value != null) {
			if (type == TYPE.USER) {
				user = (UserDTO) value;
				loadData();
			} else {
				group = (UserGroup) value;
				getView().setGroup(group);
			}
		} else {
			loadData();
		}

	}

}
