```
PUT question_knowledge
{
  "mappings": {
    "properties": {
      "id": {
        "type": "long"
      },
      "course_id": {
        "type": "keyword"
      },
      "knowledge_points": {
        "type": "nested",
        "properties": {
          "id": {
            "type": "long"
          },
          "weight": {
            "type": "double"
          }
        }
      }
    }
  }
}

```