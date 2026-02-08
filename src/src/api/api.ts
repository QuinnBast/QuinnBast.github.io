function getBaseUrl() {
  if (process.env.NODE_ENV === 'production') {
    return 'https://quinnbast.ca/';
  }
  return 'http://localhost:9000/';
}

const apiClient = async (endpoint, options = {}) => {
  const defaultHeaders = {
    "Content-Type": "application/json",
  };

  try {
    const response = await fetch(`${getBaseUrl()}${endpoint}`, {
      ...options,
      headers: { ...defaultHeaders, ...options.headers },
    });

    if (!response.ok) {
      // Handle HTTP error responses gracefully
      throw new Error(error.message || "Something went wrong");
    }

    return response;
  } catch (error) {
    // Log error and rethrow for further handling or debugging
    console.error("API error:", error);
    throw error;
  }
};

export const API = {

  async getBlogMeta(): Promise<Array<BlogPost>> {
    const response = await apiClient("api/blog/meta");
    const blogItems = await response.json();
    return blogItems.blogItems.map((blogItem: Object) => new BlogPost(blogItem))
  },
  async getAsset(path: String) {
    return await apiClient(`${path}`);
  },
  async getTrainingModules(): Promise<Array<TrainingModuleMeta>> {
    const response = await apiClient("api/training/modules");
    const trainingModules = await response.json();
    return trainingModules.modules.map((module: Object) => new TrainingModuleMeta(module))
  },
  async getModuleMeta(path: String): Promise<TrainingModuleMeta> {
    const response = await apiClient(`api/training/meta/${path}`);
    const lessonMeta = await response.json();
    return new TrainingModuleMeta(lessonMeta);
  }
};

export class BlogPost {
  public title: String
  public filePath: string
  public meta: BlogMeta

  constructor(blogItem: Object) {
    this.title = blogItem.title;
    this.filePath = blogItem.path;
    this.meta = new BlogMeta(blogItem.meta);
  }
}

export class BlogMeta {
  public title: String
  public date: Date
  public description: String
  private tags: Array<String>
  public path: String
  public filename: String

  constructor(meta: Object) {
    this.title = meta.title
    this.date = new Date(meta.date)
    this.description = meta.description
    this.tags = meta.tags
        .replaceAll("[", "")
        .replaceAll("]", "")
        .split(",")
    this.path = meta.path
    this.filename = meta.filename
  }
}

export class TrainingModuleMeta {
  public title: String
  public description: String
  public path: String
  public lessons: Array<LessonMeta>
  public lastModified: Date

  constructor(module: Object) {
    this.title = module.title
    this.path = module.path
    this.lessons = module.lessons.map((lesson) => new LessonMeta(lesson))
    this.description = module.description
    this.lastModified = new Date(module.lastUpdated)

    // Sort the lessons by number.
    this.lessons.sort((a, b) => a.lessonNumber - b.lessonNumber)
  }
}

export class LessonMeta {
  public title: String
  public fileName: String
  public path: String
  public lessonNumber: Number

  constructor(lesson: Object) {
    this.title = lesson.title
    this.path = lesson.path
    this.fileName = lesson.fileName
    this.lessonNumber = lesson.lessonNumber
  }
}
