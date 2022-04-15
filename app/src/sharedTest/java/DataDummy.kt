import com.onirutla.storyapp.data.model.story.StoryResponse

object DataDummy {
    fun generateStories(): List<StoryResponse> {
        val stories = mutableListOf<StoryResponse>()

        repeat(30) {
            stories.add(StoryResponse(id = "$it", name = "Story $it"))
        }

        return stories
    }
}