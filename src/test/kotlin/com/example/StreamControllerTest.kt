import com.example.feature.minio.MinioService
import com.example.feature.stream.AvailableStreamResponse
import com.example.feature.stream.StreamController
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals


class StreamControllerTest {
    @Test
    fun `minio service empty`() {
        val minioService = mock<MinioService> {
            on { getObjects() }.thenReturn(null)
        }
        val streamController = StreamController(minioService)
        val actual = streamController.getAvailableStreams()
        val expected = emptyList<AvailableStreamResponse>()
        assertEquals(expected, actual)
    }

}