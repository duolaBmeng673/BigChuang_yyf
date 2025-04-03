import base64
import datetime
from flask import Flask, request, jsonify, send_from_directory
from werkzeug.utils import secure_filename
import torch
from PIL import Image
from transformers import AutoTokenizer, AutoModelForCausalLM
import os

SAVE_DIR = "E:/Dachuangshujuku/picture"
UPLOAD_FOLDER = "E:/Dachuangshujuku/picture"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)  # 确保文件夹存在

app = Flask(__name__)
app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER
# 加载模型和tokenizer
model_path = "E:/Qwen/Qwen2.5-1.5B-Instruct"
tokenizer = AutoTokenizer.from_pretrained(model_path, trust_remote_code=True)
model = AutoModelForCausalLM.from_pretrained(model_path, trust_remote_code=True)

# 存储对话历史
conversation_history = []

@app.route("/chat", methods=["POST"])
def chat():
    global conversation_history

    # 获取用户输入
    data = request.get_json()
    user_message = data.get("message", "")

    # 将用户消息加入对话历史
    conversation_history.append({"role": "user", "content": user_message})

    try:
        # 使用apply_chat_template构建正确格式
        input_ids = tokenizer.apply_chat_template(
            conversation_history,
            tokenize=True,
            add_generation_prompt=True,
            return_tensors="pt"
        ).to(model.device)

        # 生成参数设置
        outputs = model.generate(
            input_ids,
            max_new_tokens=512,  # 控制生成的新token数量
            do_sample=True,
            temperature=0.7,
            top_p=0.9,
            eos_token_id=tokenizer.eos_token_id,  
            pad_token_id=tokenizer.pad_token_id   
        )

        # 解码时跳过特殊token和输入部分
        full_response = tokenizer.decode(
            outputs[0][input_ids.shape[-1]:],  #获取新生成的部分
            skip_special_tokens=True
        ).strip()

        
        last_punctuation = max(
            full_response.rfind("."),
            full_response.rfind("?"),
            full_response.rfind("!")
        )
        if last_punctuation != -1:
            clean_response = full_response[:last_punctuation+1]
        else:
            clean_response = full_response

        # 将AI回答加入对话历史
        conversation_history.append({"role": "assistant", "content": clean_response})

        return jsonify({"response": clean_response})

    except Exception as e:
        # 处理过长上下文：删除最早的两轮对话
        if "exceeds maximum" in str(e):
            conversation_history = conversation_history[2:]
            return jsonify({"response": "请再说一次"})
        return jsonify({"error": str(e)}), 500


@app.route("/upload_image", methods=["POST"])
def upload_image():
    try:
        # 解析 JSON 请求
        data = request.get_json()
        if not data or "imageBase64" not in data:
            return jsonify({"error": "Missing imageBase64"}), 400

        # 解析 Base64 图片
        image_data = base64.b64decode(data["imageBase64"])

        # 生成文件名（使用时间戳避免重复）
        file_name = datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".jpg"
        file_path = os.path.join(app.config["UPLOAD_FOLDER"], file_name)

        # 保存图片
        with open(file_path, "wb") as f:
            f.write(image_data)

        # 生成可访问的 URL
        file_url = f"http://127.0.0.1:5000/uploads/{file_name}"

        return jsonify({"message": "Image uploaded successfully", "file_path": file_url}), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


       

        # # 读取图片
        # image = Image.open(file.stream)

        # # 图像预处理
        # image_tensor = preprocess(image).unsqueeze(0)

        # # # 将图像输入到图像模型中进行预测
        # with torch.no_grad():
        #    outputs = image_model(image_tensor)
        
        # # # 计算模型输出的类别
        # # _, predicted_class = torch.max(outputs, 1)

        # # 获取标签（例如 ImageNet 类别）
        # # 你可以加载 ImageNet 的类标签来查看预测结果
        # labels_url = 'https://storage.googleapis.com/download.tensorflow.org/data/imagenet_class_index.json'
        # import requests
        # response = requests.get(labels_url)
        # labels = response.json()

        # class_id = predicted_class.item()
        # predicted_label = labels[str(class_id)][1]

        # return jsonify({"predicted_class": predicted_label})
        # return "收到图片" + filename

    # except Exception as e:
    #     return jsonify({"error": str(e)}), 500
@app.route("/uploads/<filename>")
def uploaded_file(filename):
    return send_from_directory(app.config["UPLOAD_FOLDER"], filename)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)