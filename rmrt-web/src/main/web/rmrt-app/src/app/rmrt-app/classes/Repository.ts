class Repository {

  private project:string;
  private filePath:string;
  private branch:string;
  private name:string;

  private instanceLimits:Map<String, number>;

  private lastUpdated:Date;

  private resourceModel:ResourceModel;

  private changeRequests:Array<ChangeRequest>;

  private fileSystemMappings:Array<FileSystems>;

  private keyDimensioningValues:Array<KeyDimensioningValue>;

  constructor(repository) {
  }

}

