package eagleeye.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import eagleeye.controller.MainApp;
import eagleeye.datacarving.unpack.service.UnpackDirectoryService;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Directory;
import eagleeye.model.RequestHandler;
import eagleeye.model.UIRequestHandler;

public class WorkBenchController {

	// File chooser
	private File file;
	private Label labelFilePath = new Label();
	private File directory;
	private Label labelDirPath = new Label();

	// Path to identify current case
	private String casePath = "";
	ArrayList<eagleeye.entities.File> folderStructure;
	ArrayList<Directory> TreeStructure;

	// TreeView
	private final Node rootIcon = new ImageView(new Image(getClass()
			.getResourceAsStream("Icons/treeViewRootFolderIcon.png")));
	private final Image fileIcon = new Image(getClass().getResourceAsStream(
			"Icons/fileIcon.jpg"));
	// Not applicable for exact tree view. For old testing only
	/*
	 * List<MyFile> myFiles= Arrays .<MyFile> asList(new
	 * MyFile("200482583232.6910771", ".jpg", false, false, "/UI Test"), new
	 * MyFile("CS3283 meeting notes",".txt", false, false, "/UI Test"), new
	 * MyFile( "fdcbcc689c21421c9e5abb6868884fd8", ".jpg", false,
	 * false,"/UI Test"), new MyFile(
	 * "Game Design Strategies for Collectivist Persuasion", ".pdf",
	 * false,false, "/UI Test"), //new MyFile("When you are gone", ".flv",false,
	 * false, "/UI Test"), new MyFile("ӣ��2", ".jpg", false,false, "/UI Test"));
	 */
	List<MyFile> myFiles;
	TreeItem<String> rootNode;

	@FXML
	private StackPane treeViewPane;

	// UI elements
	@FXML
	private GridPane topGridPane;

	// SearchButton
	private final Image searchIcon = new Image(getClass().getResourceAsStream(
			"Icons/seach button small.png"), 16, 16, false, false);
	@FXML
	private Button searchButton;

	// DatePicker
	private LocalDate startDate = LocalDate.parse("1992-05-08");
	private LocalDate endDate = LocalDate.now();
	private final Image calendarIcon = new Image(getClass()
			.getResourceAsStream("Icons/fileIcon.jpg"));
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;

	// Time
	private String startHour = "00";
	private String startMinute = "00";
	private String endHour = "23";
	private String endMinute = "59";
	@FXML
	private TextField startHourTf;
	@FXML
	private TextField startMinuteTf;
	@FXML
	private TextField endHourTf;
	@FXML
	private TextField endMinuteTf;

	// Menu
	@FXML
	private MenuItem newClick;
	@FXML
	private MenuItem openClick;
	@FXML
	private MenuItem saveClick;
	@FXML
	private MenuItem exitClick;

	// Function Buttons
	@FXML
	private Button contactHistoryBtn;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor.
	 */
	public WorkBenchController() {
	}

	/**
	 * The initializer.
	 */

	@FXML
	private void initialize() {

		// Initialize for DatePicker
		startDatePicker.setValue(startDate);
		endDatePicker.setValue(endDate);

		startDatePicker.setOnAction(event -> {
			startDate = startDatePicker.getValue();
			System.out.println("Selected date: " + startDate);
		});

		startDatePicker.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				startDate = localDate;
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				startDate = LocalDate.parse(dateString, dateTimeFormatter);
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});

		endDatePicker.setOnAction(event -> {
			endDate = endDatePicker.getValue();
			System.out.println("Selected date: " + endDate);
		});

		endDatePicker.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				endDate = localDate;
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				endDate = LocalDate.parse(dateString, dateTimeFormatter);
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});

		// Time
		startHourTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							startHour = startHourTf.getText();
							System.out.println("startHour is " + startHour);
						}
					}
				});
		startMinuteTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							startMinute = startMinuteTf.getText();
							System.out.println("startMinute is " + startMinute);
						}
					}
				});
		endHourTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					System.out.println("Textfield on focus");
				} else {
					endHour = endHourTf.getText();
					System.out.println("endHour is " + endHour);
				}
			}
		});
		endMinuteTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							endMinute = endMinuteTf.getText();
							System.out.println("endMinute " + endMinute);
						}
					}
				});

		// file chooser
		newClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();

				// Set extension filter
				// FileChooser.ExtensionFilter extFilter = new
				// FileChooser.ExtensionFilter("AVI files (*.avi)", "*.avi");
				// fileChooser.getExtensionFilters().add(extFilter);

				// Show open file dialog
				file = fileChooser.showOpenDialog(null);

				labelFilePath.setText(file.getPath());
				System.out.println(labelFilePath);

			}
		});

		// directory chooser
		openClick.setOnAction(this::handleOpenDirectory);

		// save
		saveClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

			}
		});

		// exit
		exitClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});

		// Search
		searchButton.setGraphic(new ImageView(searchIcon));

		// Connect to DB
		/*
		 * RequestHandler rh= new UIRequestHandler();
		 * 
		 * ArrayList<eagleeye.entities.File> dummyList= rh.getFolderStructure();
		 * myFiles = new ArrayList<MyFile>(); for(eagleeye.entities.File f:
		 * dummyList){ //"fdcbcc689c21421c9e5abb6868884fd8", ".jpg", false,
		 * false,"/UI Test" myFiles.add(new
		 * MyFile(f.getFileName(),f.getFileExt()
		 * ,f.getIsDirectory(),f.getIsModified
		 * (),f.getFilePath(),f.getCategory())); }
		 */
		/*
		 * UIRequestHandler test = new UIRequestHandler();
		 * 
		 * folderStructure = test.getFolderStructure();
		 */
		DBQueryController dbController = new DBQueryController(1);
		ArrayList<Directory> TreeStructure = dbController
				.getAllDirectoriesAndFiles();
		// System.out.println(TreeStructure.get(0).getDirectoryID());

		// System.out.println("The number of Directories: " +
		// TreeStructure.size());


		// Check if DB empty
		if (TreeStructure.size() != 0) {
			// TreeView
			rootNode = new TreeItem<String>(TreeStructure.get(0)
					.getDirectoryName(), rootIcon);
			ArrayList<Directory> CopyTreeStructure = new ArrayList<Directory>(
					TreeStructure);
			TreeView<String> tree = new TreeView<String>(rootNode);

			// Force root node ID to be 0
			TreeStructure.get(0).modifyDirectoryID(0);

			// Whenever a directory found its parent, we remove it from copied list
			while (CopyTreeStructure.size() > 0) {
				int startSize = CopyTreeStructure.size();
				for (Directory dir : CopyTreeStructure) {
					TreeItem<String> targetParent = null;
					System.out.println("Current remaining Size: "
							+ CopyTreeStructure.size());
					// check if it is root
					if (dir.getDirectoryID() == 0) {
						casePath = dir.getDirectoryName();
						this.addFiles(dir, rootNode);
						CopyTreeStructure.remove(dir);
						// System.out.println("root met, removed");
						break;
					}

					TreeItem<String> newItem = new TreeItem<String>(
							dir.getDirectoryName());

					Directory parent = findDir(TreeStructure,
							dir.getParentDirectory());

					if (parent != null) {
						targetParent = findItem(rootNode,
								parent.getDirectoryName());
					} else {
						System.out.println("Cannot find parent"
								+ dir.getParentDirectory());
					}

					if (targetParent != null) {
						// System.out.println("parent found");
						targetParent.getChildren().add(newItem);
						this.addFiles(dir, newItem);
						CopyTreeStructure.remove(dir);
						break;
					} else {
						System.out.println("cannot find parent");
					}
				}
				int endSize = CopyTreeStructure.size();
				// check if no change in size, then we print out remaining list
				// and exit
				if (startSize == endSize) {
					System.out.println("Remaining Directories:");
					for (Directory dir : CopyTreeStructure) {
						System.out.println(dir.getDirectoryName()
								+ " needed parent not found: "
								+ dir.getParentDirectory());
					}
					break;
				}
			}

			/*
			 * for (MyFile file : myFiles) { TreeItem<String> empLeaf = new
			 * TreeItem<String>(file.getName(),new ImageView(fileIcon)); boolean
			 * foundPath = false; boolean foundDep = false; for
			 * (TreeItem<String> pathNode : rootNode.getChildren()) { for
			 * (TreeItem<String> depNode : pathNode.getChildren()) { // Check if
			 * path match if (pathNode.getValue().contentEquals(file.getPath()))
			 * { // Check if format match if
			 * (depNode.getValue().contentEquals(file.getFormat())) {
			 * depNode.getChildren().add(empLeaf); foundDep = true; break; } if
			 * (!foundDep) { TreeItem<String> newDepNode = new TreeItem<String>(
			 * file.getFormat()); pathNode.getChildren().add(newDepNode);
			 * newDepNode.getChildren().add(empLeaf); } foundPath = true; break;
			 * } if (!foundPath) { TreeItem<String> newPathNode = new
			 * TreeItem<String>( file.getPath()); TreeItem<String> newDepNode =
			 * new TreeItem<String>( file.getFormat());
			 * rootNode.getChildren().add(newPathNode);
			 * newPathNode.setExpanded(true);
			 * newPathNode.getChildren().add(newDepNode);
			 * newDepNode.getChildren().add(empLeaf); } } } if (!foundPath &&
			 * !foundDep){ System.out.println("first time"); TreeItem<String>
			 * newPathNode = new TreeItem<String>( file.getPath());
			 * TreeItem<String> newDepNode = new TreeItem<String>(
			 * file.getFormat()); rootNode.getChildren().add(newPathNode);
			 * newPathNode.setExpanded(true);
			 * newPathNode.getChildren().add(newDepNode);
			 * newDepNode.getChildren().add(empLeaf); } }
			 */
			tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if (mouseEvent.getClickCount() == 2) {
						TreeItem<String> item = tree.getSelectionModel()
								.getSelectedItem();
						System.out.println("Selected Text : " + item.getValue());

						// Check if it is a file, and open
						if (item.isLeaf()) {
							String filePath = item.getValue();
							item = item.getParent();
							while(item instanceof TreeItem){
								filePath = item.getValue() + "/" + filePath;
								item = item.getParent();
							}
							System.out.println("Selected File : " + filePath);
							String location = Paths.get(".").toAbsolutePath()
									.normalize().toString();
							File currentFile = new File(location + filePath);
							try {
								Desktop.getDesktop().open(currentFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			});
			treeViewPane.getChildren().add(tree);

			// Category View

		}
	}

	/*
	 * Methods of workbench
	 */

	// Find whether a target is inside the tree of root, by recursion
	public TreeItem<String> findItem(TreeItem<String> root, String target) {
		// System.out.println("I want: " + target + " current: "
		// +root.getValue());
		TreeItem<String> result = null;

		if (root.getValue() == target) {
			// System.out.println("found: " + target + " current: "+
			// root.getValue());
			return root;
		} else if (root.getChildren().size() != 0) {
			for (TreeItem<String> sub : root.getChildren()) {
				TreeItem<String> subResult = findItem(sub, target);
				if (subResult != null) {
					return subResult;
				}
			}
		} else {
			// System.out.println("cannot find: " + target +
			// " current: "+root.getValue());
		}

		return result;
	}

	// Find Directory according to ID
	public Directory findDir(ArrayList<Directory> db, int ID) {
		for (Directory checkParent : db) {
			if (checkParent.getDirectoryID() == ID) {
				return checkParent;
			}
		}
		return null;
	}

	// Add files into directory
	public void addFiles(Directory dir, TreeItem<String> node) {
		for (eagleeye.entities.File file : dir.getFiles()) {
			TreeItem<String> newItem = new TreeItem<String>(file.getFileName() + "." + file.getFileExt(),
					new ImageView(fileIcon));
			node.getChildren().add(newItem);
		}

	}

	// private void handleStartDateClick

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// obtain current case path
		casePath = (mainApp.getCasePath());
	}

	private void handleOpenDirectory(ActionEvent event) {
		DirectoryChooser dirChooser = new DirectoryChooser();

		// Show open file dialog
		file = dirChooser.showDialog(null);

		labelDirPath.setText(file.getPath());
		System.out.println(labelDirPath);

		UnpackDirectoryService service = new UnpackDirectoryService();
		service.setDirectory(file);

		Stage dialog = this.createProgressDialog(service);

		service.start();
		dialog.show();
	}

	private Stage createProgressDialog(final Service<Void> service) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle("Import in Progress...");
		dialog.setWidth(150);
		dialog.setHeight(70);
		dialog.setResizable(false);

		VBox root = new VBox();
		root.setMaxWidth(Double.MAX_VALUE);

		Scene scene = new Scene(root);
		dialog.setScene(scene);

		final ProgressBar indicator = new ProgressBar();
		indicator.setMaxWidth(Double.MAX_VALUE);

		indicator.progressProperty().bind(service.progressProperty());
		indicator.setPrefHeight(35);
		root.getChildren().add(indicator);

		service.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable,
					State oldValue, State newValue) {
				if (newValue == State.CANCELLED || newValue == State.FAILED
						|| newValue == State.SUCCEEDED) {
					dialog.hide();
				}
			}
		});

		Button cancel = new Button("Cancel");
		cancel.setPrefHeight(35);
		cancel.setMaxWidth(Double.MAX_VALUE);
		root.getChildren().add(cancel);

		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				service.cancel();
			}
		});

		return dialog;
	}

	// Classes
	// TreeView File
	public static class MyFile {

		private String name;
		private String format;
		private boolean isDeleted;
		private boolean isModified;
		private String path;
		private String category;

		private MyFile(String name, String format, boolean isDeleted,
				boolean isModified, String path, String category) {
			this.name = name;
			this.format = format;
			this.isDeleted = isDeleted;
			this.isModified = isModified;
			this.path = path;
			this.category = category;
		}

		public String getName() {
			return name;
		}

		public void setName(String fName) {
			name = fName;
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String fName) {
			format = fName;
		}

		public boolean getIsDeleted() {
			return isDeleted;
		}

		public void setIsDeleted(boolean tf) {
			isDeleted = tf;
		}

		public boolean getIsModified() {
			return isModified;
		}

		public void setIsModified(boolean tf) {
			isModified = tf;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String fName) {
			path = fName;
		}

		public void setCategory(String fCat) {
			category = fCat;
		}

		public String getCategory() {
			String result = "";
			return result;
		}
	}

}
