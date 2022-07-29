//
//  LoginViewController.swift
//  CyxbsMobile2019_iOS
//
//  Created by 方昱恒 on 2020/10/5.
//  Copyright © 2020 Redrock. All rights reserved.
//

import UIKit
import SwiftyJSON


private enum LoginState {
    case lackAccount
    case lackPassword
    case lackAccountAndPassword
    case didNotAcceptProtocol
    case OK
}

class LoginViewController: UIViewController, UITextFieldDelegate, PrivacyTipViewDelegate {
    
    @IBOutlet private weak var stuNumTextField: UITextField! //学号
    
    @IBOutlet private weak var idNumTextField: UITextField! //密码
    
    @IBOutlet private weak var protocolCheckButton: UIButton!
    
    @IBOutlet private weak var loginTitleLabel: UILabel!
    
    @IBOutlet private weak var loginSubTitleLabel: UILabel!

    private weak var hud: MBProgressHUD!
    
    private var loginCheck: LoginState {
        
        if (idNumTextField.text == nil || idNumTextField.text == "") && (stuNumTextField.text == nil || stuNumTextField.text == "") {
            return .lackAccountAndPassword
        }
        else if idNumTextField.text == nil || idNumTextField.text == "" {
            return .lackPassword
        }
        else if stuNumTextField.text == nil || stuNumTextField.text == "" {
            return .lackAccount
        }
        else if (!protocolCheckButton.isSelected) {
            return .didNotAcceptProtocol
        }
        else {
            return .OK
        }
        
    }
    
    
    // MARK: - 构造器、生命周期
    init() {
        super.init(nibName: nil, bundle: nil)
        self.modalPresentationStyle = .fullScreen
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = true

    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.isNavigationBarHidden = true

        protocolCheckButton.backgroundColor = UIColor.clear
        protocolCheckButton.layer.cornerRadius = 8
        protocolCheckButton.clipsToBounds = true
        protocolCheckButton.layer.borderWidth = 1
        protocolCheckButton.layer.borderColor = UIColor.blue.cgColor
        
        if #available(iOS 11.0, *) {
            self.loginTitleLabel.textColor = UIColor(.dm ,light: UIColor(hexString: "#15315B")!, dark: UIColor(hexString: "#EFEFEF")!)
            self.loginSubTitleLabel.textColor = UIColor(.dm ,light: UIColor(hexString: "#15315B")!, dark: UIColor(hexString: "#EFEFEF")!)
            self.loginSubTitleLabel.alpha = 0.6;
            self.view.backgroundColor = UIColor(.dm ,light: UIColor(hexString: "#F8F9FC")!, dark: UIColor(hexString: "#000101")!)
            self.stuNumTextField.textColor = UIColor(.dm ,light: UIColor(hexString: "#1C3058")!, dark: UIColor(hexString: "#DFDFE2")!)
            self.idNumTextField.textColor = UIColor(.dm ,light: UIColor(hexString: "#1C3058")!, dark: UIColor(hexString: "#DFDFE2")!)
        } else {
            self.loginTitleLabel.textColor = UIColor(red: 21/255.0, green: 49/255.0, blue: 91/255.0, alpha: 1)
            self.loginSubTitleLabel.textColor = UIColor(red: 21/255.0, green: 49/255.0, blue: 91/255.0, alpha: 1)
            self.loginSubTitleLabel.alpha = 0.6;
            self.view.backgroundColor = UIColor(red: 248/255.0, green: 249/255.0, blue: 252/255.0, alpha: 1)
            self.stuNumTextField.textColor = UIColor(red: 28/255.0, green: 48/255.0, blue: 88/255.0, alpha: 1)
            self.idNumTextField.textColor = UIColor(red: 28/255.0, green: 48/255.0, blue: 88/255.0, alpha: 1)
        }
        showPrivacyTip()
    }
    
    // MARK: - PrivacyTipViewDelegate 的代理方法
    func showPrivacyPolicy(_ view: PrivacyTipView) {
        self.present(UserProtocolViewController(), animated: true, completion: nil)
    }
    // 点击 “同意” 按钮后调用
    func allowBtnClik(_ view: PrivacyTipView) {
        self.protocolCheckButton.isSelected = true
    }
    
    // 点击 “不同意” 按钮后调用
    func notAllowBtnClik(_ view: PrivacyTipView) {
        self.protocolCheckButton.isSelected = false
    }
    
    func showPrivacyTip() {
        let pathStr = URL.init(string: NSHomeDirectory())!.appendingPathComponent("/Documents/privacy_policy_is_showed").absoluteString
        
        let val = try? String.init(contentsOfFile: pathStr, encoding: .utf8)
        
        if val == nil {
            // 标记为已经显示过隐私协议
            try? "privacy_policy_is_showed".write(toFile: pathStr, atomically: true, encoding: .utf8)
            
            // 弹出隐私政策更新窗口
            let tipView = PrivacyTipView.init()
            tipView.delegate = self
            self.view.addSubview(tipView)
        }
    }
   
    
    
    // MARK: - 按钮
    @IBAction private func loginButtonClicked(_ sender: UIButton) {
        
        switch loginCheck {
            case .OK:
                showHud("登录中...")
                LoginModel.loginWith(stuNum: stuNumTextField.text!, idNum: idNumTextField.text!) {
                    (UIApplication.shared.delegate?.window!?.rootViewController as! UITabBarController).selectedIndex = 0
                    self.hideHud()
                    self.dismiss(animated: true, completion: nil)
                    //完成登录成功后todo的一些配置
                    TodoSyncTool.share().logInSuccess();
                } failed: { isNet in
                    self.hideHud()
                    if isNet {
                        self.showHud("请检查网络连接", time: 1)
                        
                    } else {
                        self.showHud("账号或密码错误", time: 1)
                    }
                    
                }
                
            case .lackAccount:
                showHud("请输入账号", time: 1)
                
            case .lackPassword:
                showHud("请输入密码", time: 1)
                
            case .lackAccountAndPassword:
                showHud("请输入账号和密码", time: 1)
                
            case .didNotAcceptProtocol:
                showHud("请阅读并同意《掌上重邮用户协议》", time: 1)

        }
        
    }


    @IBAction private func protocolButtonClicked(_ sender: UIButton) {
        self.present(UserProtocolViewController(), animated: true, completion: nil)
    }
    
    @IBAction private func protocolCheckButtonClicked(_ sender: UIButton) {
        sender.isSelected = !sender.isSelected
    }
    
    @IBAction func forgotPassword(_ sender: Any) {
//        let findPasswordView = FindPasswordView(frame: self.view.bounds)
//        view.addSubview(findPasswordView)
//        self.navigationController ?.pushViewController(<#T##viewController: UIViewController##UIViewController#>, animated: <#T##Bool#>)
        let getVC = YYZGetIdVC()
        self.navigationController?.pushViewController(getVC, animated: true)
    }
    
    
    // MARK: - HUD
    func showHud(_ text: String, time: TimeInterval) {
        let hud = MBProgressHUD.showAdded(to: self.view, animated: true)
        hud?.mode = MBProgressHUDMode.text
        hud?.labelText = text
        hud?.hide(true, afterDelay: time)
    }
    
    func showHud(_ text: String) {
        let hud = MBProgressHUD.showAdded(to: self.view, animated: true)
        hud?.mode = MBProgressHUDMode.text
        hud?.labelText = text
        self.hud = hud
    }
    
    func hideHud() {
        self.hud.hide(true)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
}
